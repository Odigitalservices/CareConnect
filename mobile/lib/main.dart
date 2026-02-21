import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:careconnect_mobile/core/api/api_client.dart';
import 'package:careconnect_mobile/features/auth/cubit/auth_cubit.dart';
import 'core/router/app_router.dart';

void main() => runApp(const CareConnectApp());

class CareConnectApp extends StatelessWidget {
  const CareConnectApp({super.key});

  @override
  Widget build(BuildContext context) {
    final apiClient = ApiClient();
    return BlocProvider<AuthCubit>(
      create: (_) => AuthCubit(apiClient, const FlutterSecureStorage()),
      child: MaterialApp.router(
        title: 'CareConnect',
        debugShowCheckedModeBanner: false,
        theme: ThemeData(
          colorScheme: ColorScheme.fromSeed(seedColor: const Color(0xFF1976D2)),
          useMaterial3: true,
        ),
        routerConfig: buildRouter(apiClient),
      ),
    );
  }
}
