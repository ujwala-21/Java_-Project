# BOOKLOOP – TEAM CONTRIBUTION & ARCHITECTURAL CODE OWNERSHIP

This document establishes the mandatory five-member balanced code ownership, architecture map, method responsibilities, Advanced Java concepts demonstrated, and presentation boundaries for **BookLoop: Smart Student Book Exchange & E-Commerce Management System**.

---

## TEAM MEMBER SUMMARY MATRIX

| Member | Primary Responsibility | Key Assigned Classes | Advanced Java Concepts | DB Operations | UI Screens |
|---|---|---|---|---|---|
| **Member 1** | **Authentication & User Management** | `User`, `Student`, `Admin`, `UserDAO`, `AuthenticationService`, `LoginController`, `SessionManager`, `ValidationUtil`, `PasswordUtil` | Inheritance, Polymorphism, Encapsulation, BCrypt Hashing, Regex Pattern Matching, Singleton Pattern | Parameterized `PreparedStatement`, User/Student Multi-table Transaction, Duplicate Key Checks | `LoginFrame`, `RegisterFrame`, `ProfilePanel` |
| **Member 2** | **Book Marketplace & Listing Management** | `Book`, `BookListing`, `WishlistItem`, `BookDAO`, `ListingDAO`, `WishlistDAO`, `BookService`, `ListingService`, `WishlistService`, `BookController`, `BookCard` | Swing Custom Components, SwingWorker Asynchronous Loading, Java IO/NIO File Handling, Dynamic SQL Query Building | Complex `SELECT` with dynamic filters, multi-table `JOIN`, Foreign Key Integrity, Pagination/Limit | `MarketplacePanel`, `AddListingPanel`, `MyListingsPanel`, `BookDetailsPanel`, `WishlistPanel`, `RecommendationsPanel` |
| **Member 3** | **Exchange Requests & Reservation Workflow** | `ExchangeRequest`, `ExchangeTransaction`, `Reservation`, `Review`, `ExchangeDAO`, `ReservationDAO`, `ReviewDAO`, `ExchangeService`, `ReservationService`, `ReviewService`, `ExchangeController` | ACID SQL Transactions (`setAutoCommit(false)`, `commit()`, `rollback()`), Java 8 `java.time` Duration/Period API, Event-Driven Handshake | Multi-row Transactional Handshake, Status Transitions, Cascading Updates, Average Aggregation | `ExchangeRequestsPanel`, `ReservationPanel`, `HistoryPanel`, `ReviewDialog` |
| **Member 4** | **Smart Matching, Circular Exchange & Credit Wallet** | `ExchangeGraph`, `CycleDetection`, `MatchingEngine`, `MatchingService`, `CreditWallet`, `CreditTransaction`, `CreditDAO`, `CreditService`, `WalletController`, `MatchResult` | Directed Graph Data Structure, Recursive DFS Backtracking, Cycle Detection, HashSet/HashMap Canonical Hashing, BigDecimal Financial Arithmetic | Concurrency-safe Balance Deductions, Financial Audit Trail Logging, Graph Adjacency Extraction | `CircularExchangePanel`, `WalletPanel` |
| **Member 5** | **Administration, Notifications & Analytics** | `Notification`, `NotificationDAO`, `NotificationService`, `AnalyticsDAO`, `AnalyticsService`, `AdminController`, `Course`, `Department`, `StatCard` | Java Swing `Graphics2D` Custom Chart Rendering, Aggregate SQL Queries (`COUNT`, `GROUP BY`, `COALESCE`), Observer Pattern | Aggregate Statistical Queries, Real-time Unread Counter, Role-Based Administrative Overrides | `AdminDashboardPanel`, `UserManagementPanel`, `ListingManagementPanel`, `AnalyticsPanel`, `NotificationPanel` |

---

## DETAILED MEMBER BREAKDOWNS

### MEMBER 1: AUTHENTICATION AND USER MANAGEMENT
- **Assigned Module:** Authentication, User & Student Domain, Session Handling, Security.
- **Assigned Classes:**
  - `com.bookloop.model.User`
  - `com.bookloop.model.Student`
  - `com.bookloop.model.Admin`
  - `com.bookloop.dao.UserDAO`
  - `com.bookloop.service.AuthenticationService`
  - `com.bookloop.controller.LoginController`
  - `com.bookloop.util.SessionManager`
  - `com.bookloop.util.ValidationUtil`
  - `com.bookloop.util.PasswordUtil`
  - `com.bookloop.ui.LoginFrame`
  - `com.bookloop.ui.RegisterFrame`
  - `com.bookloop.ui.student.ProfilePanel`
- **Important Methods to Explain:**
  1. `UserDAO.createStudent(Student, passwordHash)`: Demonstrates an atomic SQL transaction creating a row in `users`, linking to `students`, and provisioning a default `credit_wallets` record.
  2. `AuthenticationService.login(email, password)`: Validates credentials, verifies BCrypt salt and hash, checks active account status, and initializes the `SessionManager`.
  3. `ValidationUtil.isValidEmail()` & `isValidPassword()`: Uses Java `java.util.regex.Pattern` to enforce academic email and password complexity rules.
  4. `PasswordUtil.hash()` & `verify()`: Encapsulates `org.mindrot.jbcrypt.BCrypt` for secure cryptographic password management.
- **Viva Defense Points:**
  - Why BCrypt is preferred over SHA-256 for password security (built-in salt, work factor resistant to GPU brute-forcing).
  - How OOP inheritance is utilized between `User`, `Student`, and `Admin`.
  - How thread-safe double-checked locking is used in `SessionManager` singleton.

---

### MEMBER 2: BOOK MARKETPLACE AND LISTING MANAGEMENT
- **Assigned Module:** Catalog Architecture, Dynamic Marketplace Search, Multi-Criteria Filtering, and Listing Management.
- **Assigned Classes:**
  - `com.bookloop.model.Book`
  - `com.bookloop.model.BookListing`
  - `com.bookloop.model.WishlistItem`
  - `com.bookloop.dao.BookDAO`
  - `com.bookloop.dao.ListingDAO`
  - `com.bookloop.dao.WishlistDAO`
  - `com.bookloop.service.BookService`
  - `com.bookloop.service.ListingService`
  - `com.bookloop.service.WishlistService`
  - `com.bookloop.controller.BookController`
  - `com.bookloop.ui.components.BookCard`
  - `com.bookloop.ui.student.MarketplacePanel`
  - `com.bookloop.ui.student.AddListingPanel`
  - `com.bookloop.ui.student.MyListingsPanel`
  - `com.bookloop.ui.student.BookDetailsPanel`
  - `com.bookloop.ui.student.WishlistPanel`
  - `com.bookloop.ui.student.RecommendationsPanel`
- **Important Methods to Explain:**
  1. `ListingDAO.searchListings(query, type, condition, deptId, maxPrice, sortBy)`: Builds a parameterized dynamic SQL query avoiding SQL injection while joining `book_listings`, `books`, `departments`, and `users`.
  2. `BookCard` custom UI component: Implements responsive hover effects (`MouseAdapter`), condition badges, and wishlist quick-add callbacks.
  3. `AddListingPanel.publishListing()`: Gathers multi-part data, saves local book images via `java.nio.file.Files.copy()`, and persists condition disclosure flags.
  4. `ListingService.calculateSuggestedCredits()`: Rule-based credit estimation based on physical book condition and original price.
- **Viva Defense Points:**
  - How Swing event dispatch thread responsiveness is preserved using `SwingWorker<List<BookListing>, Void>`.
  - How dynamic query building safely sets positional parameters (`stmt.setObject(i + 1, ...)`).
  - Why honest condition transparency tags prevent post-exchange student disputes.

---

### MEMBER 3: EXCHANGE REQUESTS AND RESERVATION WORKFLOW
- **Assigned Module:** Peer-to-Peer Exchange Handshake, Two-Party Handover Confirmation, 7-Day Window Expiry, and Trust Ratings.
- **Assigned Classes:**
  - `com.bookloop.model.ExchangeRequest`
  - `com.bookloop.model.ExchangeTransaction`
  - `com.bookloop.model.Reservation`
  - `com.bookloop.model.Review`
  - `com.bookloop.dao.ExchangeDAO`
  - `com.bookloop.dao.ReservationDAO`
  - `com.bookloop.dao.ReviewDAO`
  - `com.bookloop.service.ExchangeService`
  - `com.bookloop.service.ReservationService`
  - `com.bookloop.service.ReviewService`
  - `com.bookloop.controller.ExchangeController`
  - `com.bookloop.ui.student.ExchangeRequestsPanel`
  - `com.bookloop.ui.student.ReservationPanel`
  - `com.bookloop.ui.student.HistoryPanel`
  - `com.bookloop.ui.components.ReviewDialog`
- **Important Methods to Explain:**
  1. `ExchangeService.acceptRequest(requestId, ownerId)`: Coordinates a 4-step database transaction (`conn.setAutoCommit(false)`): updates request status, reserves listing, creates reservation with 7-day expiration, and inserts student notification.
  2. `ReservationService.confirmPickup(reservationId, studentId)`: Manages the two-party confirmation state machine (`REQUESTER_CONFIRMED` + `OWNER_CONFIRMED` -> `COMPLETED`).
  3. `ExchangeService.completeExchange(reservationId, userId)`: Atomic handover closure: transfers 50 exchange credits to giver, sets listing to `COMPLETED`, and notifies both participants.
  4. `DateUtil.getRemainingDays(expiresAt)`: Uses Java 8 `java.time.Duration` to compute remaining handover days and hours.
- **Viva Defense Points:**
  - What happens when a database exception occurs mid-transaction (rollback ensures database consistency with no orphaned reservations).
  - How state transitions prevent double-booking or conflicting reservations on the same textbook.
  - How the rating system computes cumulative average scores (`AVG(rating)`).

---

### MEMBER 4: SMART MATCHING, CIRCULAR EXCHANGE & CREDIT MANAGEMENT
- **Assigned Module:** Graph Data Modeling, DFS Cycle Detection Algorithm, Direct Matching Engine, and Virtual Credit Wallet.
- **Assigned Classes:**
  - `com.bookloop.algorithm.ExchangeGraph`
  - `com.bookloop.algorithm.CycleDetection`
  - `com.bookloop.algorithm.MatchingEngine`
  - `com.bookloop.model.MatchResult`
  - `com.bookloop.model.ExchangeCycle`
  - `com.bookloop.model.CreditWallet`
  - `com.bookloop.model.CreditTransaction`
  - `com.bookloop.dao.CreditDAO`
  - `com.bookloop.service.CreditService`
  - `com.bookloop.service.MatchingService`
  - `com.bookloop.controller.WalletController`
  - `com.bookloop.ui.student.CircularExchangePanel`
  - `com.bookloop.ui.student.WalletPanel`
- **Important Methods to Explain:**
  1. `ExchangeGraph`: Adjacency list representation using `Map<Integer, List<Edge>>` where directed edge A -> B represents Student A offering an available book that Student B has in their wishlist.
  2. `CycleDetection.dfs()`: Depth-First Search with backtracking up to `maxCycleLength = 4` to detect closed directed cycles ($A \to B \to C \to A$).
  3. `CycleDetection.canonicalCycleHash()`: Generates sorted node identifiers to guarantee rotational invariance and deduplicate cycles.
  4. `CreditDAO.adjustCredits()`: Safe balance debit/credit with `balance >= amount` constraints and corresponding `credit_transactions` audit logging.
- **Viva Defense Points:**
  - Time and space complexity of DFS cycle detection on the exchange graph.
  - Why circular exchanges solve the "coincidence of wants" economic problem in peer-to-peer textbook marketplaces.
  - Why `BigDecimal` is strictly required for financial credit arithmetic rather than `double`.

---

### MEMBER 5: ADMINISTRATION, NOTIFICATIONS AND ANALYTICS
- **Assigned Module:** Admin Control Center, Live Database Reporting, Demand Analytics, and System Moderation.
- **Assigned Classes:**
  - `com.bookloop.model.Notification`
  - `com.bookloop.model.Course`
  - `com.bookloop.model.Department`
  - `com.bookloop.dao.NotificationDAO`
  - `com.bookloop.dao.AnalyticsDAO`
  - `com.bookloop.service.NotificationService`
  - `com.bookloop.service.AnalyticsService`
  - `com.bookloop.controller.AdminController`
  - `com.bookloop.ui.components.StatCard`
  - `com.bookloop.ui.components.NotificationPanel`
  - `com.bookloop.ui.admin.AdminDashboardPanel`
  - `com.bookloop.ui.admin.UserManagementPanel`
  - `com.bookloop.ui.admin.ListingManagementPanel`
  - `com.bookloop.ui.admin.AnalyticsPanel`
- **Important Methods to Explain:**
  1. `AnalyticsDAO.getSystemStatistics()`: Single-query composite SQL executing subqueries to gather aggregate counts, fulfillment efficiency, and total circular exchanges.
  2. `AnalyticsPanel.drawHorizontalBarChart()`: Custom Swing rendering with Java `Graphics2D`, anti-aliased gradient bars, and dynamic coordinate scaling.
  3. `NotificationDAO.getNotificationsForUser()`: Queries real-time notification alerts with unread counter badges.
  4. `UserManagementPanel.toggleSelectedStudent()`: Administrative toggle for account activation/suspension.
- **Viva Defense Points:**
  - How SQL aggregate functions (`COUNT`, `GROUP BY`, `COALESCE`) drive business reporting.
  - How `Graphics2D` custom painting works without relying on third-party heavy dependencies.
  - How role-based access control prevents students from invoking admin operations.
