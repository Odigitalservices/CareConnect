import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import 'package:careconnect_mobile/features/professionals/bloc/professionals_bloc.dart';
import 'package:careconnect_mobile/features/professionals/bloc/professionals_event.dart' as evt;
import 'package:careconnect_mobile/features/professionals/bloc/professionals_state.dart';
import 'package:careconnect_mobile/features/professionals/screens/widgets/professional_card.dart';

class ProfessionalsListScreen extends StatefulWidget {
  const ProfessionalsListScreen({super.key});

  @override
  State<ProfessionalsListScreen> createState() => _ProfessionalsListScreenState();
}

class _ProfessionalsListScreenState extends State<ProfessionalsListScreen> {
  final _scrollController = ScrollController();
  final _searchController = TextEditingController();

  @override
  void initState() {
    super.initState();
    context.read<ProfessionalsBloc>().add(const evt.ProfessionalsLoaded());
    _scrollController.addListener(_onScroll);
  }

  @override
  void dispose() {
    _scrollController.dispose();
    _searchController.dispose();
    super.dispose();
  }

  void _onScroll() {
    if (_scrollController.position.pixels >=
        _scrollController.position.maxScrollExtent - 200) {
      context.read<ProfessionalsBloc>().add(const evt.ProfessionalsNextPage());
    }
  }

  void _onSearch() {
    context.read<ProfessionalsBloc>().add(
      evt.ProfessionalsFiltered(
        specialization: _searchController.text.trim().isEmpty
            ? null
            : _searchController.text.trim(),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Find a Professional')),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(12),
            child: TextField(
              controller: _searchController,
              decoration: InputDecoration(
                hintText: 'Search by specialization...',
                prefixIcon: const Icon(Icons.search),
                suffixIcon: IconButton(
                  icon: const Icon(Icons.clear),
                  onPressed: () {
                    _searchController.clear();
                    _onSearch();
                  },
                ),
                border: const OutlineInputBorder(),
              ),
              onSubmitted: (_) => _onSearch(),
            ),
          ),
          Expanded(
            child: BlocBuilder<ProfessionalsBloc, ProfessionalsState>(
              builder: (context, state) {
                if (state is ProfessionalsLoading) {
                  return const Center(child: CircularProgressIndicator());
                }
                if (state is ProfessionalsError) {
                  return Center(
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Text(state.message),
                        const SizedBox(height: 8),
                        ElevatedButton(
                          onPressed: () => context
                              .read<ProfessionalsBloc>()
                              .add(const evt.ProfessionalsLoaded()),
                          child: const Text('Retry'),
                        ),
                      ],
                    ),
                  );
                }
                if (state is ProfessionalsLoaded) {
                  if (state.professionals.isEmpty) {
                    return const Center(child: Text('No professionals found'));
                  }
                  return ListView.builder(
                    controller: _scrollController,
                    itemCount: state.professionals.length + (state.hasMore ? 1 : 0),
                    itemBuilder: (context, index) {
                      if (index == state.professionals.length) {
                        return const Center(
                          child: Padding(
                            padding: EdgeInsets.all(16),
                            child: CircularProgressIndicator(),
                          ),
                        );
                      }
                      final professional = state.professionals[index];
                      return ProfessionalCard(
                        professional: professional,
                        onTap: () => context.push('/professionals/${professional.id}'),
                      );
                    },
                  );
                }
                return const SizedBox.shrink();
              },
            ),
          ),
        ],
      ),
    );
  }
}
