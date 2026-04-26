<template>
  <div id="adminUserManagePage">
    <a-card title="用户管理" :bordered="false">
      <a-form layout="inline" :model="searchParams" class="search-form">
        <a-form-item label="用户 ID">
          <a-input-number v-model:value="searchParams.id" :min="1" placeholder="请输入 ID" />
        </a-form-item>
        <a-form-item label="账号">
          <a-input v-model:value="searchParams.userAccount" placeholder="请输入账号" allow-clear />
        </a-form-item>
        <a-form-item label="昵称">
          <a-input v-model:value="searchParams.userName" placeholder="请输入昵称" allow-clear />
        </a-form-item>
        <a-form-item label="角色">
          <a-select
            v-model:value="searchParams.userRole"
            placeholder="请选择角色"
            style="width: 120px"
            allow-clear
          >
            <a-select-option value="admin">管理员</a-select-option>
            <a-select-option value="user">普通用户</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" @click="handleSearch">查询</a-button>
            <a-button @click="handleReset">重置</a-button>
            <a-button type="primary" ghost @click="openAddModal">新增用户</a-button>
          </a-space>
        </a-form-item>
      </a-form>

      <a-table
        class="table-area"
        row-key="id"
        :columns="columns"
        :data-source="dataList"
        :loading="loading"
        :pagination="pagination"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'userAvatar'">
            <a-avatar :src="record.userAvatar" />
          </template>
          <template v-else-if="column.key === 'userRole'">
            <a-tag :color="record.userRole === 'admin' ? 'gold' : 'blue'">
              {{ record.userRole === 'admin' ? '管理员' : '普通用户' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'createTime'">
            {{ formatDate(record.createTime) }}
          </template>
          <template v-else-if="column.key === 'updateTime'">
            {{ formatDate(record.updateTime) }}
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="link" @click="openEditModal(record)">编辑</a-button>
              <a-popconfirm
                title="确定删除该用户吗？"
                ok-text="删除"
                cancel-text="取消"
                @confirm="handleDelete(record)"
              >
                <a-button type="link" danger>删除</a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="editModalOpen"
      :title="isEditMode ? '编辑用户' : '新增用户'"
      :confirm-loading="submitLoading"
      @ok="handleSubmitUser"
      @cancel="closeModal"
    >
      <a-form :model="editForm" layout="vertical">
        <a-form-item label="账号" required>
          <a-input
            v-model:value="editForm.userAccount"
            placeholder="请输入账号"
            :disabled="isEditMode"
          />
        </a-form-item>
        <a-form-item label="昵称" required>
          <a-input v-model:value="editForm.userName" placeholder="请输入昵称" />
        </a-form-item>
        <a-form-item label="头像地址">
          <a-input v-model:value="editForm.userAvatar" placeholder="请输入头像 URL" />
        </a-form-item>
        <a-form-item label="简介">
          <a-textarea v-model:value="editForm.userProfile" :rows="3" placeholder="请输入用户简介" />
        </a-form-item>
        <a-form-item label="角色" required>
          <a-select v-model:value="editForm.userRole" placeholder="请选择角色">
            <a-select-option value="admin">管理员</a-select-option>
            <a-select-option value="user">普通用户</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import type { TablePaginationConfig, TableProps } from 'ant-design-vue'
import { message } from 'ant-design-vue'
import { addUser, deleteUser, listUserVoByPage, updateUser } from '@/api/userController.ts'

const loading = ref(false)
const submitLoading = ref(false)
const dataList = ref<API.UserVO[]>([])

const searchParams = reactive<API.UserQueryRequest>({
  id: undefined,
  userAccount: '',
  userName: '',
  userRole: undefined,
  pageNum: 1,
  pageSize: 5,
  sortField: 'createTime',
  sortOrder: 'descend',
})

const pagination = reactive<TablePaginationConfig>({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total: number) => `共 ${total} 条`,
})

const columns = ref<TableProps['columns']>([])

const columnTitleMap: Record<string, string> = {
  id: 'ID',
  userAvatar: '头像',
  userAccount: '账号',
  userName: '昵称',
  userProfile: '简介',
  userRole: '角色',
  createTime: '创建时间',
  updateTime: '更新时间',
}

const buildColumns = (records: API.UserVO[]) => {
  if (!records.length) {
    columns.value = [{ title: '操作', key: 'action', width: 140 }]
    return
  }
  const allKeys = new Set<string>()
  records.forEach((record) => {
    Object.keys(record as Record<string, unknown>).forEach((key) => {
      if (key !== 'userPassword') {
        allKeys.add(key)
      }
    })
  })
  allKeys.add('updateTime')
  const keys = Array.from(allKeys)
  const dynamicColumns: TableProps['columns'] = keys.map((key) => ({
    title: columnTitleMap[key] || key,
    dataIndex: key,
    key,
    ellipsis: key === 'userProfile',
    width: key === 'id' ? 80 : key === 'userAvatar' ? 90 : key === 'userRole' ? 100 : undefined,
  }))
  dynamicColumns.push({ title: '操作', key: 'action', width: 140 })
  columns.value = dynamicColumns
}

const editModalOpen = ref(false)
const isEditMode = ref(false)
const editForm = reactive<API.UserAddRequest & API.UserUpdateRequest>({
  id: undefined,
  userAccount: '',
  userName: '',
  userAvatar: '',
  userProfile: '',
  userRole: 'user',
})

const resetEditForm = () => {
  editForm.id = undefined
  editForm.userAccount = ''
  editForm.userName = ''
  editForm.userAvatar = ''
  editForm.userProfile = ''
  editForm.userRole = 'user'
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await listUserVoByPage({ ...searchParams })
    if (res.data.code === 0 && res.data.data) {
      dataList.value = res.data.data.records ?? []
      buildColumns(dataList.value)
      pagination.total = res.data.data.totalRow ?? 0
      pagination.current = searchParams.pageNum
      pagination.pageSize = searchParams.pageSize
      return
    }
    message.error(res.data.message || '获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  searchParams.pageNum = 1
  fetchData()
}

const handleReset = () => {
  searchParams.id = undefined
  searchParams.userAccount = ''
  searchParams.userName = ''
  searchParams.userRole = undefined
  searchParams.pageNum = 1
  searchParams.pageSize = 10
  fetchData()
}

const openAddModal = () => {
  isEditMode.value = false
  resetEditForm()
  editModalOpen.value = true
}

const openEditModal = (record: API.UserVO) => {
  isEditMode.value = true
  editForm.id = record.id
  editForm.userAccount = record.userAccount || ''
  editForm.userName = record.userName || ''
  editForm.userAvatar = record.userAvatar || ''
  editForm.userProfile = record.userProfile || ''
  editForm.userRole = record.userRole || 'user'
  editModalOpen.value = true
}

const closeModal = () => {
  editModalOpen.value = false
  resetEditForm()
}

const handleSubmitUser = async () => {
  if (!editForm.userAccount || !editForm.userName || !editForm.userRole) {
    message.warning('请完整填写必填项')
    return
  }
  submitLoading.value = true
  try {
    if (isEditMode.value) {
      const res = await updateUser({
        id: editForm.id,
        userName: editForm.userName,
        userAvatar: editForm.userAvatar,
        userProfile: editForm.userProfile,
        userRole: editForm.userRole,
      })
      if (res.data.code !== 0) {
        message.error(res.data.message || '更新失败')
        return
      }
      message.success('更新成功')
    } else {
      const res = await addUser({
        userAccount: editForm.userAccount,
        userName: editForm.userName,
        userAvatar: editForm.userAvatar,
        userProfile: editForm.userProfile,
        userRole: editForm.userRole,
      })
      if (res.data.code !== 0) {
        message.error(res.data.message || '新增失败')
        return
      }
      message.success('新增成功')
    }
    closeModal()
    await fetchData()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (record: API.UserVO) => {
  if (!record.id) {
    return
  }
  const res = await deleteUser({ id: record.id })
  if (res.data.code === 0) {
    message.success('删除成功')
    await fetchData()
    return
  }
  message.error(res.data.message || '删除失败')
}

const handleTableChange: TableProps['onChange'] = (pageInfo) => {
  searchParams.pageNum = pageInfo.current || 1
  searchParams.pageSize = pageInfo.pageSize || 10
  fetchData()
}

const formatDate = (time?: string) => {
  if (!time) {
    return '-'
  }
  return time.replace('T', ' ').slice(0, 19)
}

onMounted(async () => {
  await fetchData()
})
</script>

<style scoped>
#adminUserManagePage {
  width: 100%;
}

.search-form {
  margin-bottom: 16px;
}

.table-area {
  margin-top: 12px;
}
</style>
