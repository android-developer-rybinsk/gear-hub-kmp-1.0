import SwiftUI
import Shared

struct ProductDetailsScreenSwiftUI: View {
    let args: ProductDetailsArgs
    let router: RouterIOS

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Text("Детальная информация")
                    .font(.title2)
                    .fontWeight(.semibold)

                Text("Идентификатор объявления: \(args.productId)")
                    .font(.body)
                    .foregroundColor(.black)

                Text("Контент экрана будет синхронизирован с Android-версией. Пока доступно базовое представление для корректной навигации.")
                    .font(.footnote)
                    .foregroundColor(.gray)
            }
            .padding(16)
            .frame(maxWidth: .infinity, alignment: .leading)
        }
        .background(Color.white)
        .navigationTitle("Детали")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading) {
                Button(action: { router.back() }) {
                    HStack(spacing: 6) {
                        Image(systemName: "chevron.left")
                        Text("Назад")
                            .foregroundColor(.black)
                    }
                }
            }
        }
    }
}
