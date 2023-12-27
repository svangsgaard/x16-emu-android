# x16-emu-android
Google Play link:
https://play.google.com/store/apps/details?id=dk.applimate.x16emu

# How to patch

- Copy `src` directory from https://github.com/x16community/x16-emulator/ (new versions) or https://github.com/commanderx16/x16-emulator/ (old versions) into `main` directory.
  - Be sure to keep `SDL_android_main.c`
  - Some of it isn't necessary such as `javascript_interface.c`
- Replace instances of `#include "ym2151.h"` with `extern/src/ym2151.h` and `gif.h` with `extern/include/gif.h` and to `extern/include/stb_image_write.h` etc. but not in `ym2151.c`.
- Alter `main.c` to replace `*base_path` to `strncpy()` with ```
SDL_Init(SDL_INIT_VIDEO | SDL_INIT_EVENTS | SDL_INIT_GAMECONTROLLER
  #ifdef WITH_YM2151
  | SDL_INIT_AUDIO
  #endif
  );
```
- Update `CMakeLists.txt` until it works.
- Update app/src/main/assets/rom.bin with matching version.
- For weak devices, patch `void step6502()` with `clockticks6502 += 127; // Half a scanline`