import SwiftUI
import Shared

struct AuthScreenSwiftUI: View {
    @ObservedObject var vm: AuthViewModelWrapper
    let router: RouterIOS

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                switch vm.state.step {
                case let step as AuthStep.Login:
                    LoginView(step: step, state: vm.state, onAction: vm.onAction)
                case let step as AuthStep.RegisterStep1:
                    StepOneView(step: step, state: vm.state, onAction: vm.onAction)
                case let step as AuthStep.RegisterStep2:
                    StepTwoView(step: step, state: vm.state, onAction: vm.onAction)
                default:
                    EmptyView()
                }

                if let error = vm.state.errorMessage, !error.isEmpty {
                    Text(error)
                        .foregroundColor(.red)
                        .frame(maxWidth: .infinity, alignment: .leading)
                }

                if vm.state.isLoading {
                    HStack {
                        Spacer()
                        ProgressView()
                        Spacer()
                    }
                }
            }
            .padding(.vertical, 24)
        }
        .scrollDismissesKeyboard(.interactively)
        .padding(.horizontal, 16)
        .background(Color.white)
        .foregroundColor(.black)
        .navigationTitle("Авторизация")
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarBackButtonHidden(true)
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading) {
                if vm.state.step is AuthStep.RegisterStep2 {
                    Button(action: { vm.onAction(action: AuthAction.BackToStepOne()) }) {
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
}

private struct StepOneView: View {
    let step: AuthStep.RegisterStep1
    let state: AuthState
    let onAction: (AuthAction) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            InputField(
                title: "Имя пользователя",
                text: step.name,
                onChange: { onAction(AuthAction.UpdateName(value: $0)) },
                isSecure: false,
                showError: state.highlightError && step.name.isEmpty,
                submitLabel: .next
            ) {
                onAction(AuthAction.ProceedStep())
            }

            InputField(
                title: "Почта или телефон",
                text: step.login,
                onChange: { onAction(AuthAction.UpdateLogin(value: $0)) },
                isSecure: false,
                showError: state.highlightError && step.login.isEmpty,
                submitLabel: .done
            ) {
                onAction(AuthAction.ProceedStep())
            }

            Button(action: { onAction(AuthAction.ProceedStep()) }) {
                Text("Продолжить")
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 12)
                    .foregroundColor(.black)
            }
            .buttonStyle(.borderedProminent)
            .tint(Color(red: 235/255, green: 169/255, blue: 55/255))
            .disabled(state.isLoading)
        }
    }
}

private struct StepTwoView: View {
    let step: AuthStep.RegisterStep2
    let state: AuthState
    let onAction: (AuthAction) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            InputField(
                title: "Пароль",
                text: step.password,
                onChange: { onAction(AuthAction.UpdatePassword(value: $0)) },
                isSecure: true,
                showError: state.highlightError && step.password.isEmpty,
                submitLabel: .next
            ) {
                onAction(AuthAction.SubmitRegistration())
            }

            InputField(
                title: "Подтверждение пароля",
                text: step.confirmPassword,
                onChange: { onAction(AuthAction.UpdateConfirmPassword(value: $0)) },
                isSecure: true,
                showError: state.highlightError && step.confirmPassword.isEmpty,
                submitLabel: .done
            ) {
                onAction(AuthAction.SubmitRegistration())
            }

            Button(action: { onAction(AuthAction.SubmitRegistration()) }) {
                Text("Продолжить")
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 12)
                    .foregroundColor(.black)
            }
            .buttonStyle(.borderedProminent)
            .tint(Color(red: 235/255, green: 169/255, blue: 55/255))
            .disabled(state.isLoading)
        }
    }
}

private struct LoginView: View {
    let step: AuthStep.Login
    let state: AuthState
    let onAction: (AuthAction) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            InputField(
                title: "Почта",
                text: step.login,
                onChange: { onAction(AuthAction.UpdateLogin(value: $0)) },
                isSecure: false,
                showError: state.highlightError && step.login.isEmpty,
                submitLabel: .next
            ) {
                onAction(AuthAction.SubmitLogin())
            }

            InputField(
                title: "Пароль",
                text: step.password,
                onChange: { onAction(AuthAction.UpdatePassword(value: $0)) },
                isSecure: true,
                showError: state.highlightError && step.password.isEmpty,
                submitLabel: .done
            ) {
                onAction(AuthAction.SubmitLogin())
            }

            Button(action: { onAction(AuthAction.StartRegistration()) }) {
                Text("Регистрация")
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .foregroundColor(.blue)
            }

            Button(action: { onAction(AuthAction.SubmitLogin()) }) {
                Text("Войти")
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 12)
                    .foregroundColor(.black)
            }
            .buttonStyle(.borderedProminent)
            .tint(Color(red: 235/255, green: 169/255, blue: 55/255))
            .disabled(state.isLoading)
        }
    }
}

private struct InputField: View {
    let title: String
    let text: String
    let onChange: (String) -> Void
    let isSecure: Bool
    let showError: Bool
    let submitLabel: SubmitLabel
    let onSubmit: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(title)
                .font(.subheadline)
                .foregroundColor(.black)

            if isSecure {
                SecureField("", text: Binding(
                    get: { text },
                    set: { onChange($0) }
                ))
                .submitLabel(submitLabel)
                .onSubmit(onSubmit)
                .textContentType(.password)
                .foregroundColor(.black)
                .padding(12)
                .background(RoundedRectangle(cornerRadius: 10).strokeBorder(borderColor, lineWidth: 1))
            } else {
                TextField("", text: Binding(
                    get: { text },
                    set: { onChange($0) }
                ))
                .submitLabel(submitLabel)
                .onSubmit(onSubmit)
                .autocorrectionDisabled(true)
                .textInputAutocapitalization(.never)
                .foregroundColor(.black)
                .padding(12)
                .background(RoundedRectangle(cornerRadius: 10).strokeBorder(borderColor, lineWidth: 1))
            }
        }
    }

    private var borderColor: Color {
        showError ? Color.red : Color.gray.opacity(0.3)
    }
}

class AuthViewModelWrapper: ObservableObject {
    private let viewModel: AuthViewModel
    @Published var state: AuthState

    init(vm: AuthViewModel) {
        self.viewModel = vm
        self.state = vm.iosState().currentValue()

        vm.iosState().watch { [weak self] newState in
            DispatchQueue.main.async {
                self?.state = newState
            }
        }
    }

    func onAction(action: AuthAction) {
        viewModel.onAction(action: action)
    }
}
