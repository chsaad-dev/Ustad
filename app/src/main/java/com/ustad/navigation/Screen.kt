package com.ustad.navigation

sealed class Screen(val route: String) {
    // Root Graphs
    object AuthGraph : Screen("auth_graph")
    object CustomerGraph : Screen("customer_graph")
    object WorkerGraph : Screen("worker_graph")
    object AdminGraph : Screen("admin_graph")
    object DevMenu : Screen("dev_menu")

    // Auth Destinations
    object Splash : Screen("splash")
    object Login : Screen("login")
    object OtpVerification : Screen("otp_verification")
    object ProfileSetup : Screen("profile_setup")
    object RoleSelect : Screen("role_select")

    // Customer Destinations
    object CustomerHome : Screen("customer_home")
    object CreateJob : Screen("customer_create_job")
    object FindingUstad : Screen("customer_finding_ustad")
    object JobTracking : Screen("customer_job_tracking")
    object CustomerHistory : Screen("customer_history")
    object CustomerProfile : Screen("customer_profile")

    // Worker Destinations
    object WorkerDashboard : Screen("worker_dashboard")
    object WorkerRequests : Screen("worker_requests")
    object WorkerActiveJob : Screen("worker_active_job")
    object WorkerVerification : Screen("worker_verification")
    object WorkerProfile : Screen("worker_profile")

    // Admin Destinations
    object AdminOverview : Screen("admin_overview")
    object AdminVerificationQueue : Screen("admin_verifications")
    object AdminWorkerDetail : Screen("admin_worker_detail/{workerId}") {
        fun createRoute(workerId: String) = "admin_worker_detail/$workerId"
    }
    object AdminJobMonitor : Screen("admin_job_monitor")
    object AdminReports : Screen("admin_reports")
}
