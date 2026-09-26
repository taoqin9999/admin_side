const fs = require('fs');
const path = 'E:\\code\\20260926\\frontend\\src\\utils\\request.js';
let content = fs.readFileSync(path, 'utf-8');

const startMarker = '// 响应拦截';
const endMarker = 'export default request';

const startIdx = content.indexOf(startMarker);
const endIdx = content.lastIndexOf(endMarker);

const newSection = `// 响应拦截
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      if (res.code === 401) {
        // 未登录或会话过期，直接跳转登录页，不弹错误提示
        router.push('/login')
        return Promise.reject(new Error('未登录'))
      }
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    return res
  },
  error => {
    if (error.response) {
      const status = error.response.status
      if (status === 401) {
        router.push('/login')
        return Promise.reject(new Error('未登录'))
      }
      ElMessage.error(\`请求失败(\${status}): \${error.response.data?.msg || error.message}\`)
    } else {
      // 网络错误（后端不可达、跨域等）
      ElMessage.error('服务暂不可用，请检查后端服务是否正常运行')
    }
    return Promise.reject(error)
  }
)
`;

content = content.substring(0, startIdx) + newSection + '\n' + content.substring(endIdx);
fs.writeFileSync(path, content, 'utf-8');
console.log('request.js updated successfully');