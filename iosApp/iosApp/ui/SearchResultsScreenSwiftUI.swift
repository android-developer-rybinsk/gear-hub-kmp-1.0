import SwiftUI
import Shared

struct SearchResultsScreenSwiftUI: View {
    let args: SearchArgs
    let router: RouterIOS

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Text("Результаты поиска")
                    .font(.title2)
                    .fontWeight(.semibold)

                Text("Запрос: \(args.query)")
                    .font(.body)
                    .foregroundColor(.black)

                Text("Отображение поиска для iOS будет приведено в соответствие с Android. Используйте данный экран, чтобы продолжить тестирование навигации.")
                    .font(.footnote)
                    .foregroundColor(.gray)
            }
            .padding(16)
            .frame(maxWidth: .infinity, alignment: .leading)
        }
        .background(Color.white)
        .navigationTitle("Поиск")
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
