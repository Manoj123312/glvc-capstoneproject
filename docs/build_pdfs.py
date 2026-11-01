"""Generates docs/Foodie_Project_Report.pdf and docs/Foodie_Source_Code.pdf.
Usage: pip install reportlab && python docs/build_pdfs.py   (run from the repo root)
"""
import os
from xml.sax.saxutils import escape
from reportlab.lib import colors
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import cm
from reportlab.platypus import (PageBreak, Paragraph, Preformatted, SimpleDocTemplate, Spacer, Table, TableStyle)

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OUT = os.path.join(ROOT, "docs")
ss = getSampleStyleSheet()
H1 = ParagraphStyle("H1", parent=ss["Heading1"], textColor=colors.HexColor("#e23744"))
H2 = ParagraphStyle("H2", parent=ss["Heading2"], textColor=colors.HexColor("#333333"))
BODY = ParagraphStyle("B", parent=ss["BodyText"], leading=15)
CODE = ParagraphStyle("C", fontName="Courier", fontSize=6.8, leading=8.2)
CELL = ParagraphStyle("Cell", parent=ss["BodyText"], fontSize=8.5, leading=10.5)


def footer(c, d):
    c.saveState()
    c.setFont("Helvetica", 8)
    c.drawCentredString(A4[0] / 2, 1 * cm, f"Foodie Capstone Project - page {d.page}")
    c.restoreState()


def table(rows, widths, header=True):
    data = [[Paragraph(escape(str(c)), CELL) for c in r] for r in rows]
    t = Table(data, colWidths=widths, repeatRows=1 if header else 0)
    style = [("GRID", (0, 0), (-1, -1), 0.4, colors.grey), ("VALIGN", (0, 0), (-1, -1), "TOP")]
    if header:
        style.append(("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#f3d5d8")))
    t.setStyle(TableStyle(style))
    return t


def p(text):
    return Paragraph(text, BODY)


def bullets(items):
    return [Paragraph("&bull; " + i, BODY) for i in items]


def report():
    s = []
    s += [Spacer(1, 5 * cm), Paragraph("Foodie - Online Food Delivery Platform", ParagraphStyle("T", parent=H1, fontSize=26, alignment=1)),
          Spacer(1, .5 * cm), Paragraph("Java Full Stack Capstone Project", ParagraphStyle("S", parent=H2, alignment=1)),
          Paragraph("Angular 18 | Spring Boot 3 | MySQL | Docker | Jenkins | AWS EC2", ParagraphStyle("S2", parent=BODY, alignment=1)),
          Spacer(1, 1 * cm), Paragraph("Repository: https://github.com/Manoj123312/glvc-capstoneproject (branch: capstone-project)", ParagraphStyle("S3", parent=BODY, alignment=1)),
          PageBreak()]

    s += [Paragraph("1. Introduction", H1),
          p("Foodie Inc. aggregates restaurants of a city and their famous dishes on a central system. This project is a proof of concept of an "
            "online food ordering platform. Users can browse restaurants and dishes, filter them by preference, place orders and track their order "
            "history. Administrators manage users, restaurants, dishes and the order lifecycle."),
          Paragraph("1.1 Objectives", H2)]
    s += bullets(["Build a responsive Angular front end for the Admin and the End User.",
                  "Build a Spring Boot REST back end with entities, services, controllers, Swagger documentation and JUnit tests.",
                  "Design a MySQL schema and persist data using JPA/Hibernate.",
                  "Secure the system with JWT-based authentication and role-based authorisation (ADMIN / USER).",
                  "Containerise with Docker and automate build and deployment with a Jenkins pipeline on AWS EC2."])
    s += [Paragraph("1.2 Scope and roles", H2),
          table([["Role", "Capabilities"],
                 ["Admin", "Login; user management (change role, enable/disable, delete); restaurant management (add/edit/delete); product (dish) management; view all orders and update order status."],
                 ["User", "Register/login; browse restaurants and dishes; filter by restaurant, category, veg/non-veg, max price and text; add to cart; buy (place order); order history with status; cancel an order while Placed/Confirmed; edit profile."]],
                [3 * cm, 14 * cm])]

    s += [Paragraph("2. Technology Stack", H1),
          table([["Layer", "Technology", "Purpose"],
                 ["Front end", "Angular 18 (standalone components, signals, lazy routes)", "SPA UI, route guards, HTTP interceptor"],
                 ["Back end", "Java 17, Spring Boot 3.3", "REST API"],
                 ["Persistence", "Spring Data JPA / Hibernate", "ORM and repositories"],
                 ["Database", "MySQL 8 (H2 for zero-setup local run)", "Data store"],
                 ["Security", "Spring Security + JWT (jjwt), BCrypt", "Authentication / authorisation"],
                 ["API docs", "springdoc-openapi (Swagger UI)", "Interactive documentation"],
                 ["Testing", "JUnit 5, Mockito", "Service-layer unit tests"],
                 ["DevOps", "Docker, Docker Compose, Jenkins, AWS EC2", "Containerisation and CI/CD"]],
                [3 * cm, 7 * cm, 7 * cm])]

    s += [Paragraph("3. System Architecture", H1),
          p("The system is a three-tier application. The Angular SPA calls the Spring Boot REST API over HTTP/JSON. The API is organised in layers "
            "(controller, service, repository, entity) and talks to MySQL through JPA. Every protected request carries a JWT in the "
            "<font face='Courier'>Authorization: Bearer</font> header, added automatically by an Angular HTTP interceptor."),
          Preformatted("""
 Browser (Angular SPA, :4200 / nginx :80)
        |  HTTP + JSON (JWT in Authorization header)
        v
 Spring Boot API (:8080)
   Controller -> Service -> Repository -> Entity
   JwtAuthFilter | SecurityConfig (CORS open) | GlobalExceptionHandler | Swagger
        |  JDBC / JPA
        v
 MySQL 8 (foodie_db)   [H2 in-memory when profile = h2]
""", ParagraphStyle("A", fontName="Courier", fontSize=8, leading=10)),
          p("Note: the back end is delivered as one Spring Boot application with clearly separated modules (auth, users, restaurants, dishes, orders). "
            "Each module can be split into an independent microservice later without changing the API contract.")]

    s += [Paragraph("4. Database Design", H1),
          p("Tables are created automatically by Hibernate (<i>ddl-auto=update</i>); <i>database/schema.sql</i> contains the equivalent reference SQL."),
          table([["Table", "Key columns", "Relationships"],
                 ["users", "id, name, email (unique), password (BCrypt), phone, address, role (ADMIN/USER), active", "1 user : N orders"],
                 ["restaurants", "id, name, cuisine, city, address, image_url, rating", "1 restaurant : N dishes"],
                 ["dishes", "id, name, description, price, category, veg, image_url, available, restaurant_id", "N dishes : 1 restaurant"],
                 ["orders", "id, user_id, status, total_amount, delivery_address, created_at", "N orders : 1 user; 1 order : N items"],
                 ["order_items", "id, order_id, dish_id, dish_name, restaurant_name, unit_price, quantity", "Snapshot of the dish at purchase time"]],
                [3 * cm, 9 * cm, 5 * cm]),
          Spacer(1, .3 * cm),
          p("Order items store a <b>snapshot</b> of the dish name and price so that order history stays correct even if the dish is edited or deleted later. "
            "Order status values: PLACED, CONFIRMED, PREPARING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED.")]

    s += [Paragraph("5. Back-end Design", H1),
          Paragraph("5.1 Package structure", H2),
          table([["Package", "Contents"],
                 ["entity", "User, Restaurant, Dish, OrderEntity, OrderItem (JPA entities)"],
                 ["repository", "Spring Data JPA repositories incl. dynamic dish search query"],
                 ["service", "AuthService, UserService, RestaurantService, DishService, OrderService (business logic, unit tested)"],
                 ["controller", "AuthController, UserController, RestaurantController, DishController, OrderController (Swagger tagged)"],
                 ["security", "JwtService, JwtAuthFilter, AppUserDetailsService"],
                 ["config", "SecurityConfig (CORS open, role rules), OpenApiConfig, DataSeeder"],
                 ["exception", "NotFoundException, BadRequestException, GlobalExceptionHandler"],
                 ["dto", "Request/response records with Bean Validation"]],
                [3 * cm, 14 * cm]),
          Paragraph("5.2 REST API", H2),
          table([["Method", "Endpoint", "Access", "Description"],
                 ["POST", "/api/auth/register", "Public", "Register a user and return JWT"],
                 ["POST", "/api/auth/login", "Public", "Login and return JWT"],
                 ["GET", "/api/restaurants, /api/restaurants/{id}", "Public", "List / view restaurants"],
                 ["POST/PUT/DELETE", "/api/restaurants[/{id}]", "ADMIN", "Manage restaurants (delete cascades to its dishes)"],
                 ["GET", "/api/dishes?restaurantId&category&veg&maxPrice&q", "Public", "Filtered dish search"],
                 ["POST/PUT/DELETE", "/api/dishes[/{id}]", "ADMIN", "Manage dishes"],
                 ["GET", "/api/admin/users[/{id}]", "ADMIN", "List / view users"],
                 ["PUT/DELETE", "/api/admin/users/{id}", "ADMIN", "Change role/active, delete (not self)"],
                 ["GET/PUT", "/api/users/me", "Logged in", "View / update own profile"],
                 ["POST", "/api/orders", "Logged in", "Place an order (price calculated server-side)"],
                 ["GET", "/api/orders/my", "Logged in", "Order history of current user"],
                 ["PUT", "/api/orders/{id}/cancel", "Owner", "Cancel while PLACED/CONFIRMED"],
                 ["GET", "/api/admin/orders", "ADMIN", "All orders"],
                 ["PUT", "/api/admin/orders/{id}/status", "ADMIN", "Update order status"]],
                [2.6 * cm, 6.4 * cm, 2.2 * cm, 5.8 * cm]),
          Paragraph("5.3 Security", H2)]
    s += bullets(["Passwords are hashed with BCrypt; no plain-text passwords are stored.",
                  "On login a signed JWT (HS256, 24 h) is issued; <i>JwtAuthFilter</i> validates it on each request. The secret is read from the <i>JWT_SECRET</i> environment variable.",
                  "Role rules: GET restaurants/dishes are public; modifying them and everything under /api/admin/** require ADMIN; all other endpoints need authentication. Unauthenticated requests get 401, wrong role gets 403.",
                  "CORS is open (all origins, methods and headers) as required by the course.",
                  "Order totals are calculated on the server from database prices, so clients cannot tamper with prices.",
                  "A global exception handler returns consistent JSON errors (400 validation, 401, 404, 409)."])
    s += [Paragraph("5.4 Swagger and JUnit", H2),
          p("Swagger UI is available at <font face='Courier'>/swagger-ui.html</font> with a Bearer-token 'Authorize' button. "
            "Unit tests (JUnit 5 + Mockito) cover the services: OrderServiceTest (total calculation, unavailable/unknown dish, cancel rules, status update), "
            "DishServiceTest (create, search normalisation, delete, not-found) and UserAuthServiceTest (registration, duplicate email, JWT, self-delete protection, role update). "
            "16 tests, all passing.")]

    s += [Paragraph("6. Front-end Design (Angular)", H1),
          table([["Area", "File(s)", "Responsibility"],
                 ["Services", "core/services/auth.service.ts, api.services.ts, cart.service.ts", "Back-end endpoint calls (HttpClient), session and cart state"],
                 ["Interceptor", "core/guards/auth.interceptor.ts", "Adds JWT; logs out on 401"],
                 ["Guards", "core/guards/auth.guard.ts", "authGuard (logged in) and adminGuard (ADMIN)"],
                 ["Auth pages", "pages/auth/login, register", "Login and registration forms"],
                 ["User pages", "pages/user/home, cart, orders, profile", "Menu with filters, cart and checkout, order history, profile"],
                 ["Admin pages", "pages/admin/admin-restaurants, admin-dishes, admin-users, admin-orders", "CRUD and management screens"],
                 ["Shell", "app.component.ts, app.routes.ts, app.config.ts", "Navigation bar, lazy-loaded routes, providers"]],
                [2.6 * cm, 7 * cm, 7.4 * cm]),
          Spacer(1, .3 * cm),
          p("The API base URL is derived from the current host (<i>core/config.ts</i>), so the same build works on localhost and on EC2.")]

    s += [Paragraph("7. Key User Flows", H1)]
    s += bullets(["<b>Buy food:</b> Browse menu -> filter -> Add to cart -> Cart -> confirm address -> Place order -> appears in My Orders as PLACED.",
                  "<b>Track:</b> Admin changes status (CONFIRMED, PREPARING, OUT_FOR_DELIVERY, DELIVERED) in Orders; the user sees the updated status.",
                  "<b>Admin product management:</b> Admin -> Restaurants / Dishes -> add, edit, delete; changes appear immediately on the public menu.",
                  "<b>Admin user management:</b> Admin -> Users -> change role, disable/enable, delete."])

    s += [Paragraph("8. DevOps and Deployment", H1),
          p("<b>Docker:</b> the back end uses a multi-stage Maven build and a JRE image; the front end is built with Node and served by nginx. "
            "<i>docker-compose.yml</i> starts MySQL, back end and front end together with a MySQL health check."),
          p("<b>Jenkins pipeline</b> (Jenkinsfile): Checkout -> Backend tests and package -> Angular production build -> Docker build -> Docker push (Docker Hub) -> "
            "Deploy to EC2 over SSH with <i>docker-compose.prod.yml</i>."),
          p("<b>AWS EC2:</b> install Docker + Compose plugin, open ports 22/80/8080 in the security group, add Jenkins credentials "
            "(<i>dockerhub-creds</i>, <i>ec2-ssh-key</i>), set DOCKER_USER and EC2_HOST in the Jenkinsfile. The app is served at http://&lt;EC2-public-ip&gt;."),
          p("<b>Production hardening:</b> set strong JWT_SECRET and MYSQL_ROOT_PASSWORD, change the seeded admin password, restrict CORS origins and add HTTPS.")]

    s += [Paragraph("9. How to Run Locally", H1),
          p("Prerequisites: JDK 17+, Maven 3.9+, Node 18.19+/20, Angular CLI 18 (MySQL and Docker optional)."),
          Preformatted("""git clone -b capstone-project https://github.com/Manoj123312/glvc-capstoneproject.git
cd glvc-capstoneproject

# Terminal 1 - back end (H2 in memory, no DB setup)
cd backend
mvn spring-boot:run          # http://localhost:8080  |  Swagger: /swagger-ui.html

# Terminal 2 - front end
cd frontend
npm install
ng serve                     # http://localhost:4200

# MySQL mode
mvn spring-boot:run -Dspring-boot.run.profiles=mysql

# Everything in Docker
docker compose up --build    # http://localhost

# Unit tests
cd backend && mvn test""", CODE),
          p("IDE: IntelliJ - File > Open > backend folder (pom.xml), run FoodieApplication. Eclipse - Import > Existing Maven Projects. VS Code - open the frontend folder."),
          p("Demo accounts: <b>admin@foodie.com / admin123</b>, <b>user@foodie.com / user123</b>.")]

    s += [Paragraph("10. Testing and Verification", H1),
          table([["Check", "Result"],
                 ["JUnit service tests (16)", "All pass"],
                 ["Login as admin and user, JWT issued", "OK"],
                 ["USER calling /api/admin/** ", "403 Forbidden"],
                 ["Anonymous call to protected endpoint", "401 Unauthorized"],
                 ["Place order, admin updates status", "Total 498.00 computed; status DELIVERED"],
                 ["Dish filters (veg, maxPrice, q)", "Correct results"],
                 ["Swagger UI and CORS preflight", "200 / Access-Control-Allow-Origin returned"],
                 ["Angular production build", "Success"]],
                [9 * cm, 8 * cm])]

    s += [Paragraph("11. Conclusion and Future Enhancements", H1),
          p("The project delivers a complete, secure, containerised food-delivery proof of concept covering admin and user journeys. "
            "Possible enhancements: payment gateway integration, real-time order tracking (WebSocket), restaurant owner role, ratings and reviews, "
            "pagination, image upload, Angular unit/e2e tests, splitting into microservices behind an API gateway, and HTTPS with a managed database (AWS RDS).")]

    SimpleDocTemplate(os.path.join(OUT, "Foodie_Project_Report.pdf"), pagesize=A4, title="Foodie Project Report",
                      leftMargin=2 * cm, rightMargin=2 * cm, topMargin=2 * cm, bottomMargin=2 * cm).build(s, onFirstPage=footer, onLaterPages=footer)


EXT = {".java", ".ts", ".xml", ".properties", ".sql", ".yml", ".css", ".conf", ".md"}
NAMES = {"Dockerfile", "Jenkinsfile", "package.json", "angular.json", ".gitignore"}
SKIP_DIRS = {"node_modules", "target", "dist", ".angular", ".git", ".vscode", "docs"}


def source_files():
    out = []
    for d, dirs, files in os.walk(ROOT):
        dirs[:] = sorted(x for x in dirs if x not in SKIP_DIRS)
        for f in sorted(files):
            if f in NAMES or os.path.splitext(f)[1] in EXT:
                out.append(os.path.join(d, f))
    rank = lambda path: (0 if path.endswith("README.md") else 1 if "/backend/" in path else 2 if "/database/" in path else 3 if "/frontend/" in path else 4, path)
    return sorted(out, key=rank)


def source():
    files = source_files()
    s = [Spacer(1, 4 * cm), Paragraph("Foodie - Complete Source Code", ParagraphStyle("T", parent=H1, fontSize=24, alignment=1)),
         Paragraph("Repository: github.com/Manoj123312/glvc-capstoneproject (branch capstone-project)", ParagraphStyle("c", parent=BODY, alignment=1)),
         Spacer(1, 1 * cm), Paragraph("Table of contents", H2)]
    for f in files:
        s.append(Paragraph(escape(os.path.relpath(f, ROOT)), ParagraphStyle("toc", fontName="Courier", fontSize=8, leading=10)))
    s.append(PageBreak())
    for f in files:
        rel = os.path.relpath(f, ROOT)
        s.append(Paragraph(escape(rel), ParagraphStyle("fh", parent=H2, fontName="Courier-Bold", fontSize=10)))
        with open(f, encoding="utf-8") as fh:
            lines = fh.read().expandtabs(2).splitlines()
        wrapped = []
        for i, line in enumerate(lines, 1):
            line = line.rstrip()
            chunks = [line[j:j + 130] for j in range(0, max(len(line), 1), 130)]
            wrapped.append(f"{i:>4}  {chunks[0]}")
            wrapped += [f"      {c}" for c in chunks[1:]]
        for k in range(0, len(wrapped), 60):
            s.append(Preformatted("\n".join(wrapped[k:k + 60]), CODE))
        s.append(Spacer(1, .5 * cm))
    SimpleDocTemplate(os.path.join(OUT, "Foodie_Source_Code.pdf"), pagesize=A4, title="Foodie Source Code",
                      leftMargin=1.5 * cm, rightMargin=1.5 * cm, topMargin=1.5 * cm, bottomMargin=1.8 * cm).build(s, onFirstPage=footer, onLaterPages=footer)


if __name__ == "__main__":
    os.makedirs(OUT, exist_ok=True)
    report()
    source()
    print("done")
