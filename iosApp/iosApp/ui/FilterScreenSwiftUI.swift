import SwiftUI
import Shared

struct FilterScreenSwiftUI: View {
    let args: FilterArgs
    let router: RouterIOS

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                Text("Фильтр объявлений")
                    .font(.title2)
                    .fontWeight(.semibold)

                VStack(alignment: .leading, spacing: 8) {
                    Text("Категория")
                        .font(.subheadline)
                        .foregroundColor(.gray)
                    Text(categoryTitle)
                        .font(.body)
                        .foregroundColor(.black)
                }
                .frame(maxWidth: .infinity, alignment: .leading)

                Text("Настройки фильтра в iOS будут синхронизированы с Android. Используйте текущие параметры, чтобы продолжить навигацию.")
                    .font(.footnote)
                    .foregroundColor(.gray)
            }
            .padding(16)
        }
        .background(Color.white)
        .navigationTitle("Фильтр")
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

    private var categoryTitle: String {
        if let category = args.categoryId, !category.isEmpty {
            return category
        }
        return "Все категории"
    }
}
