import SwiftUI
import Shared

struct MenuScreenSwiftUI: View {
    @ObservedObject var vm: MenuViewModelWrapper
    let router: RouterIOS

    var body: some View {
        VStack {
            Text(vm.state.title)
                .font(.largeTitle)
                .padding()
                .foregroundColor(.black)
        }
        .background(.white)
    }
}

class MenuViewModelWrapper: ObservableObject {
    private let viewModel: MenuViewModel
    @Published var state: MenuState

    init(vm: MenuViewModel) {
        self.viewModel = vm
        self.state = vm.iosState().currentValue()
    }

    func dispatch(action: MenuAction) {
        viewModel.onAction(action: action)
    }
}
