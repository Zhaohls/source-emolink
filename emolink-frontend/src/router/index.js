import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/LoginPage.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/RegisterPage.vue'),
    meta: { requiresAuth: false }
  },
  // ===== 用户端路由 =====
  {
    path: '/',
    component: () => import('@/components/layout/AppLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/dashboard/DashboardPage.vue') },
      { path: 'emotion/list', name: 'DiaryList', component: () => import('@/views/emotion/DiaryListPage.vue') },
      { path: 'emotion/create', name: 'DiaryCreate', component: () => import('@/views/emotion/DiaryCreatePage.vue') },
      { path: 'emotion/detail/:id', name: 'DiaryDetail', component: () => import('@/views/emotion/DiaryDetailPage.vue') },
      { path: 'emotion/calendar', name: 'Calendar', component: () => import('@/views/emotion/CalendarPage.vue') },
      { path: 'report/daily', name: 'DailyReport', component: () => import('@/views/report/DailyReportPage.vue') },
      { path: 'report/weekly', name: 'WeeklyReport', component: () => import('@/views/report/WeeklyReportPage.vue') },
      { path: 'knowledge/list', name: 'ArticleList', component: () => import('@/views/knowledge/ArticleListPage.vue') },
      { path: 'knowledge/detail/:id', name: 'ArticleDetail', component: () => import('@/views/knowledge/ArticleDetailPage.vue') },
      { path: 'knowledge/search', name: 'SearchResult', component: () => import('@/views/knowledge/SearchResultPage.vue') },
      { path: 'treehole/list', name: 'PostList', component: () => import('@/views/treehole/PostListPage.vue') },
      { path: 'treehole/detail/:id', name: 'PostDetail', component: () => import('@/views/treehole/PostDetailPage.vue') },
      { path: 'partner/match', name: 'Match', component: () => import('@/views/partner/MatchPage.vue') },
      { path: 'partner/chat', name: 'Chat', component: () => import('@/views/partner/ChatPage.vue') },
      { path: 'profile/info', name: 'Profile', component: () => import('@/views/profile/ProfilePage.vue') },
      { path: 'profile/password', name: 'Password', component: () => import('@/views/profile/PasswordPage.vue') },
      { path: 'profile/favorites', name: 'Favorites', component: () => import('@/views/profile/FavoritesPage.vue') },
    ]
  },
  // ===== 管理后台路由 =====
  {
    path: '/admin',
    component: () => import('@/components/layout/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    meta: { requiresAdmin: true },
    children: [
      { path: 'dashboard', name: 'AdminDashboard', component: () => import('@/views/admin/AdminDashboard.vue') },
      { path: 'users', name: 'UserManage', component: () => import('@/views/admin/UserManage.vue') },
      { path: 'articles', name: 'ArticleManage', component: () => import('@/views/admin/ArticleManage.vue') },
      { path: 'article/edit', name: 'ArticleEdit', component: () => import('@/views/admin/ArticleEdit.vue') },
      { path: 'posts', name: 'PostAudit', component: () => import('@/views/admin/PostAudit.vue') },
      { path: 'comments', name: 'CommentAudit', component: () => import('@/views/admin/CommentAudit.vue') },
    ]
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  // 不需要认证的页面
  if (to.meta.requiresAuth === false) {
    return next()
  }

  // 未登录 → 跳转登录
  if (!token) {
    return next('/login')
  }

  // 管理后台页面需要 admin 角色
  if (to.matched.some(r => r.meta.requiresAdmin)) {
    if (role !== 'admin') {
      return next('/dashboard')
    }
  }

  next()
})

export default router
