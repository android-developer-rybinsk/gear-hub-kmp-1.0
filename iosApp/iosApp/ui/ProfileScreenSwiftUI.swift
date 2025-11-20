import SwiftUI
import Shared

struct ProfileScreenSwiftUI: View {
    @ObservedObject var vm: ProfileViewModelWrapper
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

class ProfileViewModelWrapper: ObservableObject {
    private let viewModel: ProfileViewModel
    @Published var state: ProfileState

    init(vm: ProfileViewModel) {
        self.viewModel = vm
        self.state = vm.iosState().currentValue()
    }

    func dispatch(action: ProfileAction) {
        viewModel.onAction(action: action)
    }
}
