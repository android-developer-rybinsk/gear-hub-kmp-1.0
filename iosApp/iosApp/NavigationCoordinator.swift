import SwiftUI
import Shared

final class NavigationCoordinator: ObservableObject {
    @Published var path: [DestinationAny] = []

    func bind(router: RouterIOS) {
        router.iosActions().watch { [weak self] action in
            DispatchQueue.main.async {
                self?.handle(action: action)
            }
        }
    }

    private func handle(action: NavigationAction) {
        if let nav = action as? NavigationAction.Navigate {
            path.append(nav.destination)
        } else if action is NavigationAction.Back {
            if !path.isEmpty { path.removeLast() }
        } else if let pop = action as? NavigationAction.PopUpTo {
            path.removeAll()
            if !pop.inclusive { path.append(pop.destination) }
        } else if let root = action as? NavigationAction.ReplaceAll {
            path.removeAll()
            path.append(root.destination)
        }
    }
}

