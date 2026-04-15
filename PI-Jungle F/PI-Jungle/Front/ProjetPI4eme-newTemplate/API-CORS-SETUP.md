# API & CORS – Configuration (port 8098)

The Angular app runs on **http://localhost:4200** and calls the API on **http://localhost:8098** (prefix `/api/v1/`).

## 1. Using the proxy (recommended)

- Start the app with: **`npm start`** (not `ng serve` without proxy).
- The proxy forwards all requests from `http://localhost:4200/api/*` to `http://localhost:8098/api/*`.
- The browser only talks to 4200 → no CORS.

If you see CORS errors to `http://localhost:8098`, either the proxy is not active (restart with `npm start`), or a request uses an absolute URL to 8098 (fix on the front).

## 2. CORS on the backend (port 8098)

If requests still go to **8098** (or for direct dev calls), the server must allow origin **http://localhost:4200**.

Exemple Spring Boot :

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
```

Or with a filter:

```java
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "*");
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, res);
        }
    }
}
```

## 3. Endpoints expected by the front

- `GET /api/v1/onlinecourses/all`
- `GET /api/v1/onsitecourses/all`
- `GET /api/v1/classrooms/all`
- `GET /api/v1/online-sessions/getAll`
- `GET /api/v1/onsite-sessions/all` (if used)
- `GET /api/v1/online-bookings/getAll`
- `GET /api/v1/onsite-bookings/all`

404 responses on `http://localhost:4200/api/...` usually mean the proxy is not applied (restart with `npm start`) or the backend on 8098 is not running.

## 4. Entity alignment (back, backoffice, frontoffice)

So that CRUD and display stay consistent with the backend and database:

- **Classrooms**: `id`, `name`, `capacity`, `location` (GET /classrooms/all list and add/update responses must include `location`).
- **Sessions**: Online = `/online-sessions/*`, On-site = `/onsite-sessions/*`. Each session must have `id`, `courseId`, `date` (or `startDate`), `capacity`, `type` (derived on front), `meetingLink` (online), `classroomId` (on-site).
- **Bookings**: Online = `/online-bookings/*`, On-site = `/onsite-bookings/*`. Each booking must have `id` (primary key for edit/delete), `sessionId`, `studentId`, `status`, `bookingDate` (or `bookedAt`).
- **Courses**: `id`, `title`, `type` (Online | On-site) to filter courses in session/booking forms.
