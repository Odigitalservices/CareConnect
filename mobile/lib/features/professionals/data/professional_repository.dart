import 'package:careconnect_mobile/features/professionals/models/paged_response.dart';
import 'package:careconnect_mobile/features/professionals/models/professional_detail.dart';
import 'package:careconnect_mobile/features/professionals/models/professional_summary.dart';

abstract class ProfessionalRepository {
  Future<PagedResponse<ProfessionalSummary>> list({
    String? city,
    String? specialization,
    double? minRate,
    double? maxRate,
    int page = 0,
    int size = 20,
  });

  Future<ProfessionalDetail> getById(String id);
}
