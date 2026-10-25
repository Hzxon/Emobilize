package ayodong.emobilize

import android.app.Application
import ayodong.emobilize.core.di.AppContainer

class EmobilizeApplication : Application() {
    val container by lazy { AppContainer(this) }
}
