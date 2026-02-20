import 'package:go_router/go_router.dart';
import 'package:careconnect_mobile/features/auth/screens/login_screen.dart';
import 'package:careconnect_mobile/features/home/screens/home_screen.dart';

final appRouter = GoRouter(
  initialLocation: '/login',
  routes: [
    GoRoute(
      path: '/login',
      builder: (context, state) => const LoginScreen(),
    ),
    GoRoute(
      path: '/home',
      builder: (context, state) => const HomeScreen(),
    ),
  ],
);
