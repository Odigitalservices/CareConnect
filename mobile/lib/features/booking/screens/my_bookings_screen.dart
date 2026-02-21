import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:intl/intl.dart';
import 'package:careconnect_mobile/features/booking/cubit/booking_cubit.dart';
import 'package:careconnect_mobile/features/booking/models/booking_model.dart';

class MyBookingsScreen extends StatefulWidget {
  const MyBookingsScreen({super.key});

  @override
  State<MyBookingsScreen> createState() => _MyBookingsScreenState();
}

class _MyBookingsScreenState extends State<MyBookingsScreen> {
  @override
  void initState() {
    super.initState();
    context.read<BookingCubit>().loadMyBookings();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('My Bookings')),
      body: BlocBuilder<BookingCubit, BookingState>(
        builder: (context, state) {
          if (state is BookingLoading) {
            return const Center(child: CircularProgressIndicator());
          }
          if (state is BookingError) {
            return Center(child: Text(state.message));
          }
          if (state is BookingsLoaded) {
            if (state.bookings.isEmpty) {
              return const Center(child: Text('No bookings yet'));
            }
            return ListView.builder(
              itemCount: state.bookings.length,
              itemBuilder: (context, index) {
                final b = state.bookings[index];
                return Card(
                  margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
                  child: ListTile(
                    title: Text(b.professionalName),
                    subtitle: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(b.specialization),
                        Text(DateFormat('MMM d, HH:mm').format(b.slotStart.toLocal())),
                      ],
                    ),
                    trailing: _StatusChip(status: b.status),
                    isThreeLine: true,
                  ),
                );
              },
            );
          }
          return const SizedBox.shrink();
        },
      ),
    );
  }
}

class _StatusChip extends StatelessWidget {
  final BookingStatus status;
  const _StatusChip({required this.status});

  @override
  Widget build(BuildContext context) {
    final color = switch (status) {
      BookingStatus.PENDING => Colors.orange,
      BookingStatus.CONFIRMED => Colors.blue,
      BookingStatus.COMPLETED => Colors.green,
      BookingStatus.CANCELLED => Colors.red,
    };
    return Chip(
      label: Text(
        status.name,
        style: const TextStyle(color: Colors.white, fontSize: 12),
      ),
      backgroundColor: color,
      padding: EdgeInsets.zero,
    );
  }
}
