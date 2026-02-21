import 'package:flutter/material.dart';
import 'package:careconnect_mobile/core/api/api_client.dart';
import 'core/router/app_router.dart';

void main() => runApp(const CareConnectApp());

class CareConnectApp extends StatelessWidget {
  const CareConnectApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      title: 'CareConnect',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: const Color(0xFF1976D2)),
        useMaterial3: true,
      ),
      routerConfig: buildRouter(ApiClient()),
    );
  }
}
