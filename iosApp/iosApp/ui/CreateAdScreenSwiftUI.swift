import SwiftUI
import Shared

struct CreateAdScreenSwiftUI: View {
    let router: RouterIOS

    var body: some View {
        VStack(spacing: 16) {
            Text("Создание объявления")
                .font(.title2)
            Button("Назад") {
                router.back()
            }
        }
        .padding()
        .background(.white)
    }
}
