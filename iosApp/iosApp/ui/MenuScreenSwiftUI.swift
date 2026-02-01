import SwiftUI
import Shared

struct MenuScreenSwiftUI: View {
    @ObservedObject var vm: MenuViewModelWrapper
    let router: RouterIOS

    var body: some View {
        VStack(spacing: 0) {
            SearchBar(
                query: vm.state.searchQuery,
                onQueryChange: { vm.dispatch(action: MenuAction.SearchChanged(query: $0)) }
            )

            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    if vm.state.isLoading && vm.state.products.isEmpty {
                        HStack {
                            Spacer()
                            ProgressView()
                            Spacer()
                        }
                        .padding(.top, 24)
                    } else if let error = vm.state.errorMessage, vm.state.products.isEmpty {
                        VStack(spacing: 12) {
                            Text(error)
                                .foregroundColor(.red)
                                .multilineTextAlignment(.center)
                            Button("Повторить") {
                                vm.dispatch(action: MenuAction.Retry())
                            }
                            .buttonStyle(.borderedProminent)
                            .tint(Color(red: 235/255, green: 169/255, blue: 55/255))
                        }
                        .frame(maxWidth: .infinity)
                        .padding(.top, 24)
                    } else {
                        CategorySection(categories: vm.state.categories)

                        SectionHeader(title: "Рекомендуем", action: "Смотреть все")

                        ProductsGrid(products: vm.state.products)

                        if vm.state.isPaginating {
                            HStack {
                                Spacer()
                                ProgressView()
                                Spacer()
                            }
                            .padding(.vertical, 12)
                        } else if let error = vm.state.errorMessage {
                            Text(error)
                                .foregroundColor(.red)
                                .frame(maxWidth: .infinity, alignment: .center)
                                .padding(.vertical, 12)
                        }
                    }
                }
                .padding(.horizontal, 20)
                .padding(.bottom, 24)
            }
        }
        .background(Color.white)
    }
}

private struct SearchBar: View {
    let query: String
    let onQueryChange: (String) -> Void

    var body: some View {
        HStack(spacing: 12) {
            TextField(
                "Поиск товаров",
                text: Binding(get: { query }, set: { onQueryChange($0) })
            )
            .textFieldStyle(.roundedBorder)
            .submitLabel(.search)

            Button(action: {}) {
                Image(systemName: "line.3.horizontal.decrease.circle")
                    .font(.system(size: 22))
            }
            .disabled(true)
        }
        .padding(.horizontal, 20)
        .padding(.vertical, 12)
        .background(Color.white)
    }
}

private struct CategorySection: View {
    let categories: [MenuCategory]

    private let columns = Array(repeating: GridItem(.flexible(), spacing: 12), count: 4)

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Категории")
                .font(.headline)

            if categories.isEmpty {
                Text("Категории пока недоступны")
                    .foregroundColor(.gray)
            } else {
                LazyVGrid(columns: columns, alignment: .leading, spacing: 12) {
                    ForEach(categories, id: \.id) { category in
                        CategoryChip(title: category.title)
                    }
                }
            }
        }
    }
}

private struct CategoryChip: View {
    let title: String

    var body: some View {
        RoundedRectangle(cornerRadius: 16)
            .fill(Color(white: 0.95))
            .frame(height: 92)
            .overlay(
                Text(title)
                    .font(.caption)
                    .multilineTextAlignment(.center)
                    .foregroundColor(.black)
                    .padding(8)
            )
    }
}

private struct SectionHeader: View {
    let title: String
    let action: String

    var body: some View {
        HStack {
            Text(title)
                .font(.headline)
            Spacer()
            Text(action)
                .foregroundColor(Color(red: 235/255, green: 169/255, blue: 55/255))
                .font(.subheadline)
        }
        .padding(.top, 4)
    }
}

private struct ProductsGrid: View {
    let products: [MenuProduct]

    private let columns = Array(repeating: GridItem(.flexible(), spacing: 12), count: 2)

    var body: some View {
        LazyVGrid(columns: columns, alignment: .leading, spacing: 14) {
            ForEach(products, id: \.id) { product in
                ProductCard(product: product)
            }
        }
    }
}

private struct ProductCard: View {
    let product: MenuProduct

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            RoundedRectangle(cornerRadius: 12)
                .fill(Color(white: 0.9))
                .frame(height: 120)
                .overlay(
                    Image(systemName: "photo")
                        .foregroundColor(.gray)
                )

            Text(product.title)
                .font(.subheadline)
                .foregroundColor(.black)
                .lineLimit(2)

            Text(priceText)
                .font(.headline)
                .foregroundColor(.black)
        }
        .padding(12)
        .background(RoundedRectangle(cornerRadius: 16).fill(Color(white: 0.98)))
    }

    private var priceText: String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .decimal
        formatter.maximumFractionDigits = 0
        let value = formatter.string(from: NSNumber(value: product.price)) ?? "\(product.price)"
        return "\(value) ₽"
    }
}

class MenuViewModelWrapper: ObservableObject {
    private let viewModel: MenuViewModel
    @Published var state: MenuState

    init(vm: MenuViewModel) {
        self.viewModel = vm
        self.state = vm.iosState().currentValue()

        vm.iosState().watch { [weak self] newState in
            DispatchQueue.main.async {
                self?.state = newState
            }
        }
    }

    func dispatch(action: MenuAction) {
        viewModel.onAction(action: action)
    }
}
