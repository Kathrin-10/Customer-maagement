# Customer Management – Current Fix Pass

## Register Project
- Converted the three-step registration form into a real one-step-at-a-time wizard.
- Step 2 and Step 3 are hidden until reached.
- Back navigation is available.
- Step validation happens before moving forward.

## Project Details
- Fixed the broken project-details page. The old file had no `#details` element, so the script crashed before rendering.
- Added payment progress and a link to the demo payment page.

## Demo Payments
- Added `payment.html` for customers.
- Added `POST /api/customer/payments`.
- A payment is linked to the selected customer project.
- The project `advanceAmount` and `lastPaymentDate` are updated.
- A linked `RevenueRecord` is created in the same transaction.
- Payments cannot exceed the remaining project balance.
- This is intentionally not a real payment gateway.

## Reports
- Revenue reports now use linked project payments.
- Legacy unlinked demo revenue rows are removed at startup.
- Existing project advance amounts are used as a fallback for older project records that pre-date linked payment records.
- Hard-coded revenue seed records were removed.
- Hard-coded generated demo projects were removed from the initializer; existing generated demo projects matching the initializer signature are cleaned on startup.

## Admin Projects
- Added `admin-projects.html`.
- Added `PUT /api/admin/projects/{id}/status`.
- Admin can set `PENDING`, `IN_PROGRESS`, or `COMPLETED`.
- Completing a project sets its completion date.
- Moving it back from completed clears the completion date.

## Customers
- Active customer table no longer displays a status column.
- Active customers are shown in the main table.
- Inactive customers are displayed separately at the bottom.
- Refresh clears search and messages before reloading.
- Dashboard customer counts now come from `/api/admin/customers/report`, not project analytics.

## Database
- Added `project_tasks.last_payment_date`.
- Added `revenue_records.project_id`.
- Added the matching JPA relationship.
- `database.sql` contains the new columns and indexes.

## Verification
- All inline JavaScript files pass `node --check` syntax validation.
- Full Maven compilation still needs to be run on the user's Windows environment because Maven 3.9.16 could not be downloaded in this environment.
