import SwiftUI
import Shared

@main
struct iOSApp: App {
    let router = RouterIOS()

    init() {
        KoinIOSBridge().doInit(router: router)
    }
    
    var body: some Scene {
        WindowGroup {
            RootContainer(router: router)
                .ignoresSafeArea()
        }
    }
}
