import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import '../../../../core/constants/app_colors.dart';
import '../../../../core/services/auth_service.dart';
import '../widgets/feature_card.dart';
import '../widgets/quick_action_button.dart';
import '../widgets/recent_activity_card.dart';

class HomePage extends ConsumerStatefulWidget {
  const HomePage({super.key});

  @override
  ConsumerState<HomePage> createState() => _HomePageState();
}

class _HomePageState extends ConsumerState<HomePage>
    with TickerProviderStateMixin {
  late AnimationController _animationController;
  late Animation<double> _fadeAnimation;
  late Animation<Offset> _slideAnimation;

  @override
  void initState() {
    super.initState();
    _animationController = AnimationController(
      duration: const Duration(milliseconds: 1000),
      vsync: this,
    );

    _fadeAnimation = Tween<double>(
      begin: 0.0,
      end: 1.0,
    ).animate(CurvedAnimation(
      parent: _animationController,
      curve: Curves.easeInOut,
    ));

    _slideAnimation = Tween<Offset>(
      begin: const Offset(0, 0.3),
      end: Offset.zero,
    ).animate(CurvedAnimation(
      parent: _animationController,
      curve: Curves.easeOutCubic,
    ));

    _animationController.forward();
  }

  @override
  void dispose() {
    _animationController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final authState = ref.watch(authServiceProvider);

    return Scaffold(
      backgroundColor: AppColors.background,
      body: SafeArea(
        child: FadeTransition(
          opacity: _fadeAnimation,
          child: SlideTransition(
            position: _slideAnimation,
            child: CustomScrollView(
              slivers: [
                // En-tête personnalisé
                _buildSliverAppBar(authState),

                // Contenu principal
                SliverToBoxAdapter(
                  child: Padding(
                    padding: const EdgeInsets.all(24),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        // Actions rapides
                        _buildQuickActions(),

                        const SizedBox(height: 32),

                        // Fonctionnalités principales
                        _buildMainFeatures(),

                        const SizedBox(height: 32),

                        // Accès rapide aux sections
                        _buildQuickAccess(),

                        const SizedBox(height: 32),
                      ],
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildSliverAppBar(AsyncValue<AuthState> authState) {
    return SliverAppBar(
      expandedHeight: 120,
      floating: false,
      pinned: true,
      backgroundColor: Colors.transparent,
      elevation: 0,
      flexibleSpace: FlexibleSpaceBar(
        background: Container(
          decoration: const BoxDecoration(
            gradient: LinearGradient(
              colors: [AppColors.background, AppColors.surface],
              begin: Alignment.topCenter,
              end: Alignment.bottomCenter,
            ),
          ),
          child: SafeArea(
            child: Padding(
              padding: const EdgeInsets.all(24),
              child: Row(
                children: [
                  CircleAvatar(
                    radius: 24,
                    backgroundColor: Colors.white.withOpacity(0.2),
                    child: const Icon(
                      Icons.person,
                      color: Colors.white,
                      size: 24,
                    ),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Text(
                          authState.value?.user?.fullName ?? 'Bonjour !',
                          style:
                              Theme.of(context).textTheme.titleLarge?.copyWith(
                                    color: Colors.white,
                                    fontWeight: FontWeight.w600,
                                  ),
                        ),
                        Text(
                          'Bienvenue sur TV Connect',
                          style:
                              Theme.of(context).textTheme.bodyMedium?.copyWith(
                                    color: Colors.white.withOpacity(0.9),
                                  ),
                        ),
                      ],
                    ),
                  ),
                  IconButton(
                    onPressed: () {
                      context.go('/notifications');
                    },
                    icon: const Icon(
                      Icons.notifications_outlined,
                      color: Colors.white,
                      size: 24,
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildQuickActions() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Actions rapides',
          style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                fontWeight: FontWeight.bold,
                color: Colors.white,
              ),
        ),
        const SizedBox(height: 16),
        Row(
          children: [
            Expanded(
              child: QuickActionButton(
                icon: Icons.payment,
                label: 'Paiement',
                onTap: () {
                  context.go('/payments');
                },
              ),
            ),
            const SizedBox(width: 16),
            Expanded(
              child: QuickActionButton(
                icon: Icons.report_problem,
                label: 'Réclamation',
                onTap: () {
                  context.go('/complaints');
                },
              ),
            ),
          ],
        ),
        const SizedBox(height: 16),
        Row(
          children: [
            Expanded(
              child: QuickActionButton(
                icon: Icons.notifications,
                label: 'Notifications',
                onTap: () {
                  context.go('/notifications');
                },
              ),
            ),
            const SizedBox(width: 16),
            Expanded(
              child: QuickActionButton(
                icon: Icons.person,
                label: 'Profil',
                onTap: () {
                  context.go('/profile');
                },
              ),
            ),
          ],
        ),
      ],
    );
  }

  Widget _buildMainFeatures() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Services disponibles',
          style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                fontWeight: FontWeight.bold,
                color: Colors.white,
              ),
        ),
        const SizedBox(height: 16),
        GridView.count(
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          crossAxisCount: 2,
          crossAxisSpacing: 16,
          mainAxisSpacing: 16,
          childAspectRatio: 1.2,
          children: [
            FeatureCard(
              icon: Icons.receipt,
              title: 'Factures',
              description: 'Consultez vos factures',
              color: AppColors.primary,
              onTap: () {
                context.go('/payments');
              },
            ),
            FeatureCard(
              icon: Icons.payment,
              title: 'Paiements',
              description: 'Effectuez vos paiements',
              color: AppColors.secondary,
              onTap: () {
                context.go('/payments');
              },
            ),
            FeatureCard(
              icon: Icons.report_problem,
              title: 'Support',
              description: 'Signalez un problème',
              color: AppColors.accent,
              onTap: () {
                context.go('/complaints');
              },
            ),
            FeatureCard(
              icon: Icons.notifications,
              title: 'Alertes',
              description: 'Recevez des notifications',
              color: AppColors.info,
              onTap: () {
                context.go('/notifications');
              },
            ),
          ],
        ),
      ],
    );
  }

  Widget _buildQuickAccess() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Accès rapide',
          style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                fontWeight: FontWeight.bold,
                color: Colors.white,
              ),
        ),
        const SizedBox(height: 16),
        const RecentActivityCard(
          title: 'Historique des paiements',
          subtitle: 'Consultez vos paiements récents',
          icon: Icons.history,
          color: AppColors.primary,
        ),
        const SizedBox(height: 12),
        const RecentActivityCard(
          title: 'Réclamations',
          subtitle: 'Signalez un problème',
          icon: Icons.report_problem,
          color: AppColors.accent,
        ),
        const SizedBox(height: 12),
        const RecentActivityCard(
          title: 'Notifications',
          subtitle: 'Dernières alertes',
          icon: Icons.notifications,
          color: AppColors.info,
        ),
      ],
    );
  }
}
