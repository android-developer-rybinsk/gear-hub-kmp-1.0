import SwiftUI
import Shared

struct MyAdsScreenSwiftUI: View {
    @ObservedObject var vm: MyAdsViewModelWrapper
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

class MyAdsViewModelWrapper: ObservableObject {
    private let viewModel: MyAdsViewModel
    @Published var state: MyAdsState

    init(vm: MyAdsViewModel) {
        self.viewModel = vm
        self.state = vm.iosState().currentValue()
    }

    func dispatch(action: MyAdsAction) {
        viewModel.onAction(action: action)
    }
}
