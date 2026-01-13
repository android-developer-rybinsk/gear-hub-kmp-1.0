import SwiftUI
import Shared

struct RootContainer: View {
    @StateObject private var nav = NavigationCoordinator()
    let router: RouterIOS

    var body: some View {
        NavigationStack(path: $nav.path) {
            SplashScreenSwiftUI(
                vm: SplashViewModelWrapper(vm: KoinKt.resolveSplashVM()),
                router: router
            )
            .navigationDestination(for: Destination.self) { dest in
                switch dest {
                case is DestinationApp.MainScreen:
                    MainScreenSwiftUI(
                        vm: MainViewModelWrapper(vm: KoinKt.resolveMainVM()),
                        router: router
                    )
                case is DestinationChats.ChatsScreen:
                    ChatsScreenSwiftUI(
                        vm: ChatsViewModelWrapper(vm: KoinKt.resolveChatsVM()),
                        router: router
                    )
                case is DestinationMenu.MenuScreen:
                    MenuScreenSwiftUI(
                        vm: MenuViewModelWrapper(vm: KoinKt.resolveMenuVM()),
                        router: router
                    )
                case is DestinationProducts.MyProductsScreen:
                    MyProductsScreenSwiftUI(
                        vm: MyProductsViewModelWrapper(vm: KoinKt.resolveMyProductsVM()),
                        router: router
                    )
                case is DestinationProfile.ProfileScreen:
                    ProfileScreenSwiftUI(
                        vm: ProfileViewModelWrapper(vm: KoinKt.resolveProfileVM()),
                        router: router
                    )
                case let dest as DestinationMenu.FilterScreen:
                    FilterScreenSwiftUI(args: dest.args, router: router)
                case let dest as DestinationMenu.DetailsScreen:
                    ProductDetailsScreenSwiftUI(args: dest.args, router: router)
                case let dest as DestinationMenu.SearchResultsScreen:
                    SearchResultsScreenSwiftUI(args: dest.args, router: router)
                case is DestinationApp.AuthScreen:
                    AuthScreenSwiftUI(
                        vm: AuthViewModelWrapper(vm: KoinKt.resolveAuthVM()),
                        router: router
                    )
                case is DestinationApp.SplashScreen:
                    SplashScreenSwiftUI(
                        vm: SplashViewModelWrapper(vm: KoinKt.resolveSplashVM()),
                        router: router
                    )
                default:
                    EmptyView()
                }
            }
        }
        .onAppear { nav.bind(router: router) }
    }
}
