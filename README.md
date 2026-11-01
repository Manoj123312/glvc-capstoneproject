# 🍔 Foodie – Food Delivery Full‑Stack Capstone (Angular + Spring Boot + MySQL)

| Layer | Tech |
|---|---|
| Frontend | Angular 18 (standalone components, signals, HttpClient, route guards, JWT interceptor) |
| Backend | Java 17, Spring Boot 3.3, Spring Security (JWT), Spring Data JPA, Swagger/OpenAPI, JUnit 5 + Mockito |
| Database | MySQL 8 (or in‑memory H2 for zero‑setup local run) |
| DevOps | Docker, Docker Compose, Jenkins pipeline, AWS EC2 |

## Features
**Admin** (login `admin@foodie.com` / `admin123`): user management (change role, enable/disable, delete), restaurant management, product (dish) management, view all orders and update order status.
**User** (login `user@foodie.com` / `user123`, or register): browse restaurants & dishes, filter (restaurant, category, veg/non‑veg, max price, search), add to cart, place order, order history with status, cancel (while Placed/Confirmed), edit profile.

## Backend structure
`entity` → `repository` → `service` (JUnit tests in `src/test`) → `controller` (Swagger annotated) · `security` (JWT) · `config` (CORS open, OpenAPI, data seeder) · `exception` (global handler).

Main endpoints (full list in Swagger):
| Method | URL | Access |
|---|---|---|
| POST | `/api/auth/register`, `/api/auth/login` | public |
| GET | `/api/restaurants`, `/api/dishes?restaurantId=&category=&veg=&maxPrice=&q=` | public |
| POST/PUT/DELETE | `/api/restaurants`, `/api/dishes` | ADMIN |
| GET/PUT/DELETE | `/api/admin/users` | ADMIN |
| GET / PUT | `/api/admin/orders`, `/api/admin/orders/{id}/status` | ADMIN |
| POST / GET | `/api/orders`, `/api/orders/my` ; PUT `/api/orders/{id}/cancel` | USER |
| GET / PUT | `/api/users/me` | logged in |

Swagger UI: http://localhost:8080/swagger-ui.html (click **Authorize**, paste the token from `/api/auth/login`).

---
## 1. Download the project
```bash
git clone https://github.com/Manoj123312/glvc-capstoneproject.git
cd glvc-capstoneproject
```
(or download ZIP: GitHub → **Code → Download ZIP**, then extract.)

## 2. Prerequisites (install once)
| Tool | Version | Check |
|---|---|---|
| JDK | 17 or newer | `java -version` |
| Maven | 3.9+ (or use the IDE's bundled Maven) | `mvn -v` |
| Node.js | 18.19+ / 20 LTS | `node -v` |
| Angular CLI | 18 | `npm i -g @angular/cli@18` then `ng version` |
| MySQL 8 | *optional* – only for MySQL mode | `mysql --version` |
| Docker Desktop | *optional* – for container run | `docker -v` |

## 3. Open in an IDE
**Backend – IntelliJ IDEA**: `File → Open` → select the `backend` folder (the one containing `pom.xml`) → *Trust Project* → wait for Maven import. Set `File → Project Structure → SDK` to JDK 17+. Open `FoodieApplication.java` and click ▶ **Run**.
**Backend – Eclipse/STS**: `File → Import → Maven → Existing Maven Projects` → choose `backend` → Finish → right‑click `FoodieApplication` → *Run As → Java Application*.
**Frontend – VS Code**: `File → Open Folder…` → choose `frontend`. Open the terminal (`Ctrl+\``).

## 4. Run locally (no database needed – H2 default)
**Terminal 1 – backend**
```bash
cd backend
mvn spring-boot:run
```
Wait for `Started FoodieApplication`. API: http://localhost:8080 · Swagger: http://localhost:8080/swagger-ui.html
(Sample admin, user, 3 restaurants and 8 dishes are seeded automatically. H2 data resets on every restart.)

**Terminal 2 – frontend**
```bash
cd frontend
npm install
ng serve
```
Open http://localhost:4200 .

### Run with MySQL instead of H2
1. Start MySQL and make sure root password matches (default `root`), or set env vars `DB_USERNAME`, `DB_PASSWORD`, `DB_URL`.
2. (Optional) run `database/schema.sql`; otherwise the app creates the DB and tables itself.
3. Start backend with the `mysql` profile:
```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```
(IntelliJ: Run configuration → *Active profiles* = `mysql`.)

### Run unit tests
```bash
cd backend && mvn test
```

## 5. Run everything with Docker (MySQL + backend + frontend)
```bash
docker compose up --build
```
Frontend → http://localhost · Backend → http://localhost:8080 · Swagger → http://localhost:8080/swagger-ui.html

## 6. CI/CD – Jenkins → Docker Hub → AWS EC2
1. EC2 (Amazon Linux/Ubuntu): install Docker + Compose plugin; open inbound ports **22, 80, 8080** in the security group.
2. Jenkins: install plugins *Pipeline, Git, Docker Pipeline, SSH Agent*; add credentials `dockerhub-creds` (Docker Hub user/password) and `ec2-ssh-key` (EC2 private key). Jenkins host needs JDK 17, Maven, Node 20 and Docker.
3. Edit `DOCKER_USER` and `EC2_HOST` at the top of `Jenkinsfile`, create a *Pipeline from SCM* job pointing to this repo.
4. Build: tests → Angular build → Docker images → push → `docker compose up -d` on EC2 using `docker-compose.prod.yml`. App is then at `http://<EC2-public-ip>`.
5. Production: set a strong `JWT_SECRET` and `MYSQL_ROOT_PASSWORD` on the EC2 host and change the seeded admin password.

## 7. Troubleshooting
| Problem | Fix |
|---|---|
| `Port 8080 already in use` | Stop the other app, or run with `--server.port=8081` (and update `frontend/src/app/core/config.ts`). |
| `release version 17 not supported` | Your JDK is older than 17 – install JDK 17+. |
| `Access denied for user 'root'` (MySQL) | Set `DB_PASSWORD` to your MySQL root password. |
| Frontend shows "Cannot reach the server" | Backend isn't running on :8080. |
| `npm install` errors | Use Node 18.19+ / 20 LTS; delete `node_modules` and retry. |
| `ng: command not found` | `npm i -g @angular/cli@18`, or use `npx ng serve`. |

The frontend calls the backend at `http://<current-host>:8080/api` (see `frontend/src/app/core/config.ts`); CORS is open on the backend.
