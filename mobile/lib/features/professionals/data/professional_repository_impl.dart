import 'package:careconnect_mobile/core/api/api_client.dart';
import 'package:careconnect_mobile/features/professionals/data/professional_repository.dart';
import 'package:careconnect_mobile/features/professionals/models/paged_response.dart';
import 'package:careconnect_mobile/features/professionals/models/professional_detail.dart';
import 'package:careconnect_mobile/features/professionals/models/professional_summary.dart';

class ProfessionalRepositoryImpl implements ProfessionalRepository {
  final ApiClient _apiClient;

  ProfessionalRepositoryImpl(this._apiClient);

  @override
  Future<PagedResponse<ProfessionalSummary>> list({
    String? city,
    String? specialization,
    double? minRate,
    double? maxRate,
    int page = 0,
    int size = 20,
  }) async {
    final params = <String, dynamic>{
      'page': page,
      'size': size,
      if (city != null) 'city': city,
      if (specialization != null) 'specialization': specialization,
      if (minRate != null) 'minRate': minRate,
      if (maxRate != null) 'maxRate': maxRate,
    };
    final response = await _apiClient.getWithQuery<Map<String, dynamic>>(
      '/api/professionals',
      queryParameters: params,
    );
    final body = response.data as Map<String, dynamic>;
    return PagedResponse.fromJson(
      body['data'] as Map<String, dynamic>,
      ProfessionalSummary.fromJson,
    );
  }

  @override
  Future<ProfessionalDetail> getById(String id) async {
    final response = await _apiClient.get<Map<String, dynamic>>('/api/professionals/$id');
    final body = response.data as Map<String, dynamic>;
    return ProfessionalDetail.fromJson(body['data'] as Map<String, dynamic>);
  }
}
