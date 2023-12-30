#ifndef __APPLE__
#define _XOPEN_SOURCE   600
#define _POSIX_C_SOURCE 1
#endif

#include "files.h"

#include <limits.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <SDL.h>
#include <zlib.h>
#include <inttypes.h>

struct x16file
{
	char path[PATH_MAX];

	SDL_RWops *file;
	int64_t size;
	int64_t pos;
	bool modified;

	struct x16file *next;
};

struct x16file *open_files = NULL;

static bool 
get_tmp_name(char *path_buffer, const char *original_path, char const *extension)
{
	if(strlen(original_path) > PATH_MAX - strlen(extension)) {
		printf("Path too long, cannot create temp file: %s\n", original_path);
		return false;
	}

	strcpy(path_buffer, original_path);
	strcat(path_buffer, extension);

	return true;
}

bool 
file_is_compressed_type(char const *path)
{
	int len = (int)strlen(path);

	if (strcmp(path + len - 3, ".gz") == 0 || strcmp(path + len - 3, "-gz") == 0) {
		return true;
	} else if (strcmp(path + len - 2, ".z") == 0 || strcmp(path + len - 2, "-z") == 0 || strcmp(path + len - 2, "_z") == 0 || strcmp(path + len - 2, ".Z") == 0) {
		return true;
	}

	return false;
}

const char *
file_find_extension(const char *path, const char *mark)
{
	if(path == NULL) {
		return NULL;
	}

	if(mark == NULL) {
		mark = path + strlen(path);
		if(file_is_compressed_type(path)) {
			mark -= 3;
		}
	}

	while(mark > path) {
		if(*mark == '.') {
			return mark;
		}
		--mark;
	}

	return NULL;
}


void
files_shutdown()
{
	struct x16file *f = open_files;
	struct x16file *next_f = NULL;
	for(; f != NULL; f = next_f) {
		next_f = f->next;
		x16close(f);
	}
}

struct x16file *
x16open(const char *path, const char *attribs)
{
	struct x16file *f = malloc(sizeof(struct x16file));
	strcpy(f->path, path);

    f->file = SDL_RWFromFile(path, attribs);
    if(f->file == NULL) {
        goto error;
    }
    f->size = SDL_RWsize(f->file);

	f->pos = 0;
	f->modified = false;
	f->next = open_files ? open_files : NULL;
	open_files = f;

	return f;

error:
	free(f);
	return NULL;
}

void 
x16close(struct x16file *f)
{
	if(f == NULL) {
		return;
	}

	SDL_RWclose(f->file);


	free(f);
}

int64_t 
x16size(struct x16file *f)
{
	if(f == NULL) {
		return 0;
	}

	return f->size;
}

int 
x16seek(struct x16file *f, int64_t pos, int origin)
{
	if(f == NULL) {
		return 0;
	}
	switch(origin) {
		case SEEK_SET:
			f->pos = (pos > f->size) ? f->size : pos;
			break;
		case SEEK_CUR:
			f->pos += pos;
			if(f->pos > f->size || f->pos < 0) {
				f->pos = f->size;
			}
			break;
		case SEEK_END:
			f->pos = f->size - pos;
			if(f->pos < 0) {
				f->pos = f->size;
			}
	}
	return SDL_RWseek(f->file, f->pos, SEEK_SET);
}

int64_t 
x16tell(struct x16file *f)
{
	if(f == NULL) {
		return 0;
	}
	return f->pos;
}

int 
x16write8(struct x16file *f, uint8_t val)
{
	if(f == NULL) {
		return 0;
	}
	int written = SDL_RWwrite(f->file, &val, 1, 1);
	f->pos += written;
	return written;
}

uint8_t 
x16read8(struct x16file *f)
{
	if(f == NULL) {
		return 0;
	}
	uint8_t val;
	int read = SDL_RWread(f->file, &val, 1, 1);
	f->pos += read;
	return read;
}

uint64_t 
x16write(struct x16file *f, const uint8_t *data, uint64_t data_size, uint64_t data_count)
{
	if(f == NULL) {
		return 0;
	}
	int64_t written = SDL_RWwrite(f->file, data, data_size, data_count);
	if(written) {
		f->modified = true;
	}
	f->pos += written * data_size;
	return written;
}

uint64_t 
x16read(struct x16file *f, uint8_t *data, uint64_t data_size, uint64_t data_count)
{
	if(f == NULL) {
		return 0;
	}
	int64_t read = SDL_RWread(f->file, data, data_size, data_count);
	f->pos += read * data_size;
	return read;
}
