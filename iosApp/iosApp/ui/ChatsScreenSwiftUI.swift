import SwiftUI
import Shared

struct ChatsScreenSwiftUI: View {
    @ObservedObject var vm: ChatsViewModelWrapper
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

class ChatsViewModelWrapper: ObservableObject {
    private let viewModel: ChatsViewModel
    @Published var state: ChatsState

    init(vm: ChatsViewModel) {
        self.viewModel = vm
        self.state = vm.iosState().currentValue()
    }

    func onAction(action: ChatsAction) {
        viewModel.onAction(action: action)
    }
}
