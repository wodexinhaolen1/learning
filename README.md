# 智慧农业系统项目说明

## 1. 项目概述

这是一个基于 Spring Boot + Vue.js 的智慧农业系统，采用前后端分离架构。项目包含用户认证、实时天气显示、访问统计等功能。

## 2. 技术栈

### 2.1 后端技术
- Spring Boot：Java 后端框架
- Spring Security：安全认证框架
- Spring Data JPA：数据库访问框架
- MySQL：数据库
- JWT：用户认证令牌
- Swagger：API 文档生成工具

### 2.2 前端技术
- Vue.js：前端框架
- Element Plus：UI 组件库
- Axios：HTTP 请求库
- ECharts：图表库
- Vue Router：路由管理

## 3. 项目结构

### 3.1 后端结构 (backgroundagriculture)
```
src/main/java/com/example/backgroundagriculture/
├── config/                 # 配置文件目录
│   ├── SecurityConfig.java # 安全配置
│   └── SwaggerConfig.java  # API文档配置
├── controller/            # 控制器目录
│   ├── UserController.java      # 用户相关接口
│   └── StatisticsController.java # 统计相关接口
├── dto/                   # 数据传输对象
│   ├── LoginRequest.java
│   └── LoginResponse.java
├── entity/               # 实体类
│   └── User.java
├── repository/           # 数据访问层
│   └── UserRepository.java
├── service/              # 服务层
│   ├── UserService.java
│   └── impl/UserServiceImpl.java
└── util/                 # 工具类
    ├── JwtUtil.java
    └── ValidationUtil.java
```

### 3.2 前端结构 (agriculture-frontend)
```
src/
├── api/                  # API 请求目录
│   ├── user.js          # 用户相关API
│   └── statistics.js    # 统计相关API
├── components/          # 组件目录
│   ├── Clock.vue       # 时钟组件
│   └── TemperatureChart.vue # 温度图表组件
├── views/              # 页面目录
│   ├── Login.vue      # 登录页面
│   └── Register.vue   # 注册页面
└── utils/             # 工具目录
    └── validator.js   # 表单验证工具
```

## 4. 核心功能流程

### 4.1 用户认证流程
1. 用户在前端输入用户名和密码
2. 前端通过 API 发送登录请求到后端
3. 后端验证用户名和密码
4. 验证成功后生成 JWT 令牌
5. 前端保存令牌并在后续请求中使用

```javascript
// 前端登录请求示例 (api/user.js)
login(username, password) {
    return api.post('/users/login', { username, password })
}

// 前端保存令牌 (views/Login.vue)
const handleLogin = async () => {
    const response = await userApi.login(username, password)
    localStorage.setItem('token', response.data.token)
}
```

```java
// 后端登录处理 (UserServiceImpl.java)
public LoginResponse login(LoginRequest request) {
    User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new BusinessException("用户名或密码错误"));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new BusinessException("用户名或密码错误");
    }

    String token = jwtUtil.generateToken(user.getUsername());
    return new LoginResponse(token, user);
}
```

### 4.2 访问统计功能
1. 用户登录时记录访问量
2. 前端定期请求获取最新统计数据
3. 后端维护在线用户列表和总访问量

```javascript
// 前端获取统计数据 (api/statistics.js)
getVisitorStats() {
    return axios.get(`${BASE_URL}/api/statistics/visitors`)
}
```

```java
// 后端统计服务 (VisitorStatisticsService.java)
public class VisitorStatisticsService {
    private final AtomicInteger totalVisitors = new AtomicInteger(0);
    private final ConcurrentHashMap<String, Long> activeUsers = new ConcurrentHashMap<>();

    public void addVisitor() {
        totalVisitors.incrementAndGet();
    }

    public void addActiveUser(String username) {
        activeUsers.put(username, System.currentTimeMillis());
    }
}
```

## 5. 前后端交互

### 5.1 API 请求配置
前端使用 Axios 发送 HTTP 请求，统一配置如下：

```javascript
// api/user.js
const api = axios.create({
    baseURL: 'http://localhost:8080/api'
})

// 添加请求拦截器，自动添加认证令牌
api.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    return config
})
```

### 5.2 后端接口定义
使用 Spring MVC 注解定义 API 接口：

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody UserRegisterRequest request) {
        userService.register(request);
        return ResponseEntity.ok().build();
    }
}
```

## 6. 安全配置

### 6.1 前端安全
- 使用 HTTPS 传输
- 密码加密传输
- Token 存储在 localStorage
- 路由守卫验证登录状态

### 6.2 后端安全
- Spring Security 配置
- 密码加密存储
- JWT 令牌验证
- 接口权限控制

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/users/login").permitAll()
                .requestMatchers("/api/users/register").permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
```

## 7. 开发环境搭建

### 7.1 后端环境
1. 安装 JDK 17
2. 安装 Maven
3. 配置 MySQL 数据库
4. 导入项目到 IDE（推荐 IntelliJ IDEA）

### 7.2 前端环境
1. 安装 Node.js
2. 安装 npm 或 yarn
3. 进入前端目录
4. 运行 `npm install` 安装依赖
5. 运行 `npm run dev` 启动开发服务器

## 8. 调试技巧

### 8.1 前端调试
- 使用 Vue DevTools
- 使用浏览器开发者工具
- 查看 Network 面板的请求响应
- 使用 console.log 打印调试信息

### 8.2 后端调试
- 使用 IDE 断点调试
- 查看日志输出
- 使用 Swagger UI 测试 API
- 使用 Postman 测试接口

## 9. 常见问题

### 9.1 跨域问题
前后端分离开发时常见的跨域问题，通过以下方式解决：

1. 后端配置 CORS：
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("*"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

2. 前端配置代理：
```javascript
// vite.config.js
export default defineConfig({
    server: {
        proxy: {
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true
            }
        }
    }
})
```

### 9.2 认证问题
- 检查 token 是否正确设置
- 检查 token 是否过期
- 确认请求头中是否包含 Authorization

### 9.3 数据刷新问题
- 使用 Vue 的响应式特性
- 实现定时刷新机制
- 监听相关事件

## 10. 项目扩展

### 10.1 可以添加的功能
- 用户角色管理
- 农作物监控
- 环境数据采集
- 自动化控制
- 数据可视化
- 预警系统

### 10.2 性能优化
- 添加缓存层
- 优化数据库查询
- 使用消息队列
- 实现数据分页
- 优化前端资源加载

## 11. 学习资源

### 11.1 相关技术文档
- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [Vue.js 官方文档](https://vuejs.org/)
- [Element Plus 组件库](https://element-plus.org/)
- [Axios 文档](https://axios-http.com/)
- [JWT 介绍](https://jwt.io/)

### 11.2 推荐学习路线
1. 基础知识
   - HTML/CSS/JavaScript
   - Java 基础
   - SQL 基础

2. 框架学习
   - Spring Boot
   - Vue.js
   - Spring Security

3. 工具使用
   - Git
   - Maven
   - IDE
   - 调试工具

4. 项目实践
   - 跟随教程
   - 修改代码
   - 添加功能
   - 解决问题 