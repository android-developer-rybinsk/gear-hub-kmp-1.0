import SwiftUI
import Shared

struct MainScreenSwiftUI: View {
    @ObservedObject var vm: MainViewModelWrapper
    let router: RouterIOS

    @State private var selectedTab: DestinationAny = DestinationApp.MenuScreen()

    var body: some View {
        VStack(spacing: 0) {
            Group {
                switch selectedTab {
                case is DestinationApp.MenuScreen:
                    let menuVM = KoinKt.resolveMenuVM()
                    MenuScreenSwiftUI(vm: MenuViewModelWrapper(vm: menuVM), router: router)

                case is DestinationProducts.MyAdsScreen:
                    let adsVM = KoinKt.resolveMyAdsVM()
                    MyAdsScreenSwiftUI(vm: MyAdsViewModelWrapper(vm: adsVM), router: router)

                case is DestinationChats.ChatsScreen:
                    let chatsVM = KoinKt.resolveChatsVM()
                    ChatsScreenSwiftUI(vm: ChatsViewModelWrapper(vm: chatsVM), router: router)

                case is DestinationProfile.ProfileScreen:
                    let profileVM = KoinKt.resolveProfileVM()
                    ProfileScreenSwiftUI(vm: ProfileViewModelWrapper(vm: profileVM), router: router)

                default:
                    Text("Unknown tab")
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)

            // Нижняя навигация
            HStack {
                TabButton(
                    icon: "icon_test",
                    label: "Главная",
                    isSelected: selectedTab is Destination.MenuScreen
                ) { selectedTab = Destination.MenuScreen() }

                TabButton(
                    icon: "icon_test",
                    label: "Объявления",
                    isSelected: selectedTab is Destination.MyAdsScreen
                ) { selectedTab = Destination.MyAdsScreen() }

                TabButton(
                    icon: "icon_test",
                    label: "Сообщения",
                    isSelected: selectedTab is Destination.ChatsScreen
                ) { selectedTab = Destination.ChatsScreen() }

                TabButton(
                    icon: "icon_test",
                    label: "Профиль",
                    isSelected: selectedTab is Destination.ProfileScreen
                ) { selectedTab = Destination.ProfileScreen() }
            }
            .padding(.vertical, 8)
            .safeAreaInset(edge: .bottom) {
                Color.clear.frame(height: 0)
            }
            .background(Color(red: 16/255, green: 16/255, blue: 16/255)) // #101010
        }
        .edgesIgnoringSafeArea(.bottom)
        .navigationBarHidden(true)
        .background(Color.white)
    }
}

struct TabButton: View {
    let icon: String
    let label: String
    let isSelected: Bool
    let action: () -> Void

    var body: some View {
        VStack(spacing: 4) {
            ZStack {
                if isSelected {
                    Circle()
                        .fill(Color(red: 235/255, green: 169/255, blue: 55/255)) // #EBA937
                        .frame(width: 40, height: 40)
                }
                Image(icon)
                    .renderingMode(.template)
                    .resizable()
                    .scaledToFit()
                    .frame(width: 24, height: 24)
                    .foregroundColor(isSelected ? .white : Color.white.opacity(0.6))
            }
            Text(label)
                .font(.system(size: isSelected ? 14 : 12, weight: isSelected ? .bold : .regular))
                .foregroundColor(isSelected ? .white : Color.white.opacity(0.6))
        }
        .frame(maxWidth: .infinity)
        .onTapGesture { action() }
    }
}

class MainViewModelWrapper: ObservableObject {
    private let viewModel: MainViewModel
    @Published var state: MainState

    init(vm: MainViewModel) {
        self.viewModel = vm
        self.state = vm.iosState().currentValue()
    }
}
