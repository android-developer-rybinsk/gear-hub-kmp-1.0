import SwiftUI
import Shared

struct MyProductsScreenSwiftUI: View {
    @ObservedObject var vm: MyProductsViewModelWrapper
    let router: RouterIOS

    var body: some View {
        VStack {
            Text(vm.state.title)
                .font(.largeTitle)
                .padding()
                .foregroundColor(.black)
            Button("Добавить") {
                vm.dispatch(action: MyProductsAction.CreateAd())
            }
        }
        .background(.white)
    }
}

class MyProductsViewModelWrapper: ObservableObject {
    private let viewModel: MyProductsViewModel
    @Published var state: MyProductsState

    init(vm: MyProductsViewModel) {
        self.viewModel = vm
        self.state = vm.iosState().currentValue()
    }

    func dispatch(action: MyProductsAction) {
        viewModel.onAction(action: action)
    }
}
