# Small Business Finance Dashboard

A complete Android app for small business finance tracking with dashboard, charts, transaction management, and Excel export capabilities.

## Features

- **Dashboard**: Shows income, expenses, and net profit in real-time
- **Bar Chart**: Visual comparison of income vs expenses
- **Pie Chart**: Expense breakdown by category
- **Transaction List**: Recent transactions with delete option
- **Add Transaction**: Form with categories, date picker, and notes
- **Excel Export**: 4-sheet professional report (.xlsx)
- **Reports Screen**: Detailed charts for income and expense categories

## Quick Actions

- **+ Income**: Quickly add income transactions
- **+ Expense**: Quickly add expense transactions  
- **Export**: Generate and share Excel reports

## Categories

### Income Categories
- Sales, Services, Consulting, Investments, Refunds, Other Income

### Expense Categories
- Inventory, Salaries, Rent, Utilities, Marketing, Equipment, Supplies, Transport, Insurance, Taxes, Maintenance, Other Expense

## Technical Details

| Property | Value |
|----------|-------|
| Language | Kotlin |
| Min Android | 7.0 (API 24) |
| Architecture | MVVM with Room Database |
| Charts | MPAndroidChart library |
| Excel | Apache POI library |

## How to Build & Run

1. Clone this repository
2. Open Android Studio → File → Open → Select the project folder
3. Wait for Gradle sync to complete (may take 2-3 minutes on first build)
4. Connect your Android device (USB debugging enabled) or start an emulator
5. Click the green Run button ▶️

## Offline Capability

The app works completely offline and stores all your business data securely on your device using Room Database. No internet connection required!

## License

MIT License