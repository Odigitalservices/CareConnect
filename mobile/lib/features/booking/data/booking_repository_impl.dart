import 'package:careconnect_mobile/core/api/api_client.dart';
import 'package:careconnect_mobile/features/booking/data/booking_repository.dart';
import 'package:careconnect_mobile/features/booking/models/booking_model.dart';

class BookingRepositoryImpl implements BookingRepository {
  final ApiClient _apiClient;
  BookingRepositoryImpl(this._apiClient);

  @override
  Future<BookingModel> create(String slotId, String visitType) async {
    final response = await _apiClient.post<Map<String, dynamic>>(
      '/api/bookings',
      data: {'slotId': slotId, 'visitType': visitType},
    );
    final body = response.data as Map<String, dynamic>;
    return BookingModel.fromJson(body['data'] as Map<String, dynamic>);
  }

  @override
  Future<List<BookingModel>> getMyBookings() async {
    final response = await _apiClient.get<Map<String, dynamic>>('/api/bookings');
    final body = response.data as Map<String, dynamic>;
    return (body['data'] as List<dynamic>)
        .map((e) => BookingModel.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  @override
  Future<BookingModel> cancel(String bookingId) async {
    final response = await _apiClient.patch<Map<String, dynamic>>(
      '/api/bookings/$bookingId/cancel',
    );
    final body = response.data as Map<String, dynamic>;
    return BookingModel.fromJson(body['data'] as Map<String, dynamic>);
  }
}
