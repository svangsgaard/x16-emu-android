cmake_minimum_required(VERSION 3.4.1)

project(x16-emulator)

include_directories(
    ${CMAKE_CURRENT_SOURCE_DIR}/../SDL2/include

    # SDL_internal.h is located in src required by SDL_android_main.c bridge
    ${CMAKE_CURRENT_SOURCE_DIR}/../SDL2/src

)

add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/../SDL2/ ${CMAKE_CURRENT_SOURCE_DIR}/../SDL2)


SET(CMAKE_C_FLAGS "-Ofast -DWITH_YM2151 -Isrc/extern/include")
SET(CMAKE_CXX_FLAGS "-Ofast -DWITH_YM2151 -Isrc/extern/ymfm/src")

add_library(
    main-lib

    SHARED

    # Provides a relative path to your source file(s).
    src/SDL_android_main.c

    src/main.c
    src/audio.c
    src/cartridge.c
    src/debugger.c
    src/disasm.c
    src/files.c
    src/i2c.c
    src/icon.c
    src/ieee.c
    src/iso_8859_15.c
    src/joystick.c
    src/keyboard.c
    src/memory.c
    src/rendertext.c
    src/rtc.c
    src/sdcard.c
    src/serial.c
    src/smc.c
    src/testbench.c
    src/timing.c
    src/vera_pcm.c
    src/vera_psg.c
    src/vera_spi.c
    src/video.c
    src/via.c
    src/wav_recorder.c
    src/ymglue.cpp

    src/cpu/fake6502.c

    src/extern/ymfm/src/ymfm_adpcm.cpp
    src/extern/ymfm/src/ymfm_opl.cpp
    src/extern/ymfm/src/ymfm_opm.cpp
    src/extern/ymfm/src/ymfm_pcm.cpp

)

target_link_libraries(
    main-lib
    SDL2

)