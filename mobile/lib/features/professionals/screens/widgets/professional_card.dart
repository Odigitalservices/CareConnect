import 'package:flutter/material.dart';
import 'package:careconnect_mobile/features/professionals/models/professional_summary.dart';

class ProfessionalCard extends StatelessWidget {
  final ProfessionalSummary professional;
  final VoidCallback onTap;

  const ProfessionalCard({
    super.key,
    required this.professional,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
      child: ListTile(
        onTap: onTap,
        title: Text(professional.fullName,
            style: Theme.of(context).textTheme.titleMedium),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(professional.specialization),
            if (professional.city != null) Text(professional.city!),
          ],
        ),
        trailing: professional.hourlyRate != null
            ? Text('${professional.hourlyRate!.toStringAsFixed(0)} MAD/h',
                style: Theme.of(context).textTheme.bodySmall)
            : null,
        isThreeLine: professional.city != null,
      ),
    );
  }
}
