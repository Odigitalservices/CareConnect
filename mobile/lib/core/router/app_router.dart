import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:careconnect_mobile/core/api/api_client.dart';
import 'package:careconnect_mobile/features/auth/screens/login_screen.dart';
import 'package:careconnect_mobile/features/booking/cubit/booking_cubit.dart';
import 'package:careconnect_mobile/features/booking/data/booking_repository_impl.dart';
import 'package:careconnect_mobile/features/booking/screens/my_bookings_screen.dart';
import 'package:careconnect_mobile/features/home/screens/home_screen.dart';
import 'package:careconnect_mobile/features/professionals/bloc/professional_detail_cubit.dart';
import 'package:careconnect_mobile/features/professionals/bloc/professionals_bloc.dart';
import 'package:careconnect_mobile/features/professionals/data/professional_repository_impl.dart';
import 'package:careconnect_mobile/features/professionals/screens/professional_detail_screen.dart';
import 'package:careconnect_mobile/features/professionals/screens/professionals_list_screen.dart';

GoRouter buildRouter(ApiClient apiClient) {
  final repository = ProfessionalRepositoryImpl(apiClient);

  return GoRouter(
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
      GoRoute(
        path: '/professionals',
        builder: (context, state) => BlocProvider(
          create: (_) => ProfessionalsBloc(repository),
          child: const ProfessionalsListScreen(),
        ),
      ),
      GoRoute(
        path: '/professionals/:id',
        builder: (context, state) => MultiBlocProvider(
          providers: [
            BlocProvider<ProfessionalDetailCubit>(
              create: (_) => ProfessionalDetailCubit(repository),
            ),
            BlocProvider<BookingCubit>(
              create: (_) => BookingCubit(BookingRepositoryImpl(apiClient)),
            ),
          ],
          child: ProfessionalDetailScreen(
            id: state.pathParameters['id']!,
          ),
        ),
      ),
      GoRoute(
        path: '/bookings',
        builder: (context, state) => BlocProvider<BookingCubit>(
          create: (_) => BookingCubit(BookingRepositoryImpl(apiClient)),
          child: const MyBookingsScreen(),
        ),
      ),
    ],
  );
}
