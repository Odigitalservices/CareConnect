import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:intl/intl.dart';
import 'package:careconnect_mobile/features/professionals/bloc/professional_detail_cubit.dart';
import 'package:careconnect_mobile/features/professionals/models/professional_detail.dart';

class ProfessionalDetailScreen extends StatefulWidget {
  final String id;

  const ProfessionalDetailScreen({super.key, required this.id});

  @override
  State<ProfessionalDetailScreen> createState() => _ProfessionalDetailScreenState();
}

class _ProfessionalDetailScreenState extends State<ProfessionalDetailScreen> {
  @override
  void initState() {
    super.initState();
    context.read<ProfessionalDetailCubit>().load(widget.id);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Professional Profile')),
      body: BlocBuilder<ProfessionalDetailCubit, ProfessionalDetailState>(
        builder: (context, state) {
          if (state is ProfessionalDetailLoading) {
            return const Center(child: CircularProgressIndicator());
          }
          if (state is ProfessionalDetailError) {
            return Center(child: Text(state.message));
          }
          if (state is ProfessionalDetailLoaded) {
            final p = state.professional;
            return SingleChildScrollView(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(p.fullName,
                      style: Theme.of(context).textTheme.headlineMedium),
                  const SizedBox(height: 4),
                  Text(p.specialization,
                      style: Theme.of(context).textTheme.titleMedium),
                  if (p.city != null) ...[
                    const SizedBox(height: 4),
                    Row(children: [
                      const Icon(Icons.location_on, size: 16),
                      const SizedBox(width: 4),
                      Text(p.city!),
                    ]),
                  ],
                  if (p.hourlyRate != null) ...[
                    const SizedBox(height: 4),
                    Text('${p.hourlyRate!.toStringAsFixed(0)} MAD/hour',
                        style: Theme.of(context).textTheme.bodyLarge),
                  ],
                  if (p.bio != null && p.bio!.isNotEmpty) ...[
                    const SizedBox(height: 16),
                    Text('About', style: Theme.of(context).textTheme.titleMedium),
                    const SizedBox(height: 8),
                    Text(p.bio!),
                  ],
                  const SizedBox(height: 24),
                  Text('Available Slots',
                      style: Theme.of(context).textTheme.titleMedium),
                  const SizedBox(height: 8),
                  if (p.availableSlots.isEmpty)
                    const Text('No available slots at the moment')
                  else
                    Wrap(
                      spacing: 8,
                      runSpacing: 8,
                      children: p.availableSlots
                          .map((slot) => _SlotChip(slot: slot))
                          .toList(),
                    ),
                ],
              ),
            );
          }
          return const SizedBox.shrink();
        },
      ),
    );
  }
}

class _SlotChip extends StatelessWidget {
  final AvailabilitySlot slot;

  const _SlotChip({required this.slot});

  @override
  Widget build(BuildContext context) {
    final fmt = DateFormat('MMM d, HH:mm');
    return Chip(
      label: Text(
        '${fmt.format(slot.startTime.toLocal())} – ${DateFormat('HH:mm').format(slot.endTime.toLocal())}',
      ),
    );
  }
}
