import 'package:equatable/equatable.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:careconnect_mobile/core/api/api_client.dart';
import 'package:careconnect_mobile/core/storage/token_storage.dart';

// States
abstract class AuthState extends Equatable {
  const AuthState();
  @override
  List<Object?> get props => [];
}

class AuthInitial extends AuthState { const AuthInitial(); }
class AuthLoading extends AuthState { const AuthLoading(); }
class AuthSuccess extends AuthState { const AuthSuccess(); }
class AuthError extends AuthState {
  final String message;
  const AuthError(this.message);
  @override
  List<Object?> get props => [message];
}

// Cubit
class AuthCubit extends Cubit<AuthState> {
  final ApiClient _apiClient;
  final TokenStorage _storage;

  AuthCubit(this._apiClient, this._storage) : super(const AuthInitial());

  Future<void> register({
    required String email,
    required String password,
    required String firstName,
    required String lastName,
    String? phone,
  }) async {
    emit(const AuthLoading());
    try {
      final response = await _apiClient.post<Map<String, dynamic>>(
        '/api/auth/register',
        data: {
          'email': email,
          'password': password,
          'firstName': firstName,
          'lastName': lastName,
          'role': 'PATIENT',
          if (phone != null && phone.isNotEmpty) 'phone': phone,
        },
      );
      final data = (response.data as Map<String, dynamic>)['data'] as Map<String, dynamic>;
      await _storage.write(data['accessToken'] as String);
      emit(const AuthSuccess());
    } catch (e) {
      emit(AuthError(e.toString()));
    }
  }

  Future<void> login(String email, String password) async {
    emit(const AuthLoading());
    try {
      final response = await _apiClient.post<Map<String, dynamic>>(
        '/api/auth/login',
        data: {'email': email, 'password': password},
      );
      final data = (response.data as Map<String, dynamic>)['data'] as Map<String, dynamic>;
      await _storage.write(data['accessToken'] as String);
      emit(const AuthSuccess());
    } catch (e) {
      emit(AuthError(e.toString()));
    }
  }

  Future<void> logout() async {
    await _storage.delete();
    emit(const AuthInitial());
  }
}
