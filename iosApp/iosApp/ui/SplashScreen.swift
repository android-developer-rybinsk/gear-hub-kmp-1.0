import SwiftUI
import Shared
import Combine

struct SplashScreenSwiftUI: View {
    @ObservedObject var vm: SplashViewModelWrapper
    let router: RouterIOS

    @State private var carOffsetX: CGFloat = -600
    @State private var bikeOffsetX: CGFloat = -600

    var body: some View {
        ZStack {
            Color(red: 0.04, green: 0.16, blue: 0.25) // #0A2841
                .ignoresSafeArea()

            VStack {
                Spacer()

                VStack {
                    Image("gear_hub")
                        .resizable()
                        .frame(width: 200, height: 200)

                    Text("GearHub")
                        .font(.system(size: 40, weight: .bold))
                        .foregroundColor(.white)
                }

                Spacer()

                ZStack {
                    Image("auto")
                        .resizable()
                        .frame(width: 400, height: 300)
                        .offset(x: carOffsetX)

                    Image("moto")
                        .resizable()
                        .frame(width: 200, height: 200)
                        .offset(x: bikeOffsetX, y: 20)
                        .padding(.trailing, 150)
                }
                .padding(.bottom, 48)
            }
        }
        .onAppear {
            startAnimations()
            vm.onAction(.OnStartTimeout())
        }
        .onChange(of: vm.state.isTimeout) { _, newValue in
            if newValue {
                print("setState -> isTimeout=true")
                vm.onAction(.OnEndTimeout())
            }
        }
    }

    private func startAnimations() {
        withAnimation(.easeInOut(duration: 1.0)) {
            carOffsetX = 0
        }
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
            withAnimation(.easeInOut(duration: 1.0)) {
                bikeOffsetX = 0
            }
        }
    }
}

class SplashViewModelWrapper: ObservableObject {
    private let viewModel: SplashViewModel
    @Published var state: SplashState

    init(vm: SplashViewModel) {
        self.viewModel = vm
        self.state = vm.iosState().currentValue()

        vm.iosState().watch { [weak self] newState in
            DispatchQueue.main.async {
                self?.state = newState
            }
        }
    }

    func onAction(_ action: SplashAction) {
        viewModel.onAction(action: action)
    }
}
