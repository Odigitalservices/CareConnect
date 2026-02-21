import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Home')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            ElevatedButton(
              onPressed: () => context.push('/professionals'),
              child: const Text('Find a Professional'),
            ),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: () => context.push('/bookings'),
              child: const Text('My Bookings'),
            ),
          ],
        ),
      ),
    );
  }
}
