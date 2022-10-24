class BfsPath {
  /**
 * 拼接路径
 * @param cwd 
 * @param path 
 * @returns 
 */
  join(cwd: string, path: string) {
    return `${cwd}/${path}`.replace(/(\/+)/, '/')
  }

  resolve(...paths: string[]) {
    let resolvePath = '';
    let isAbsolutePath = false;
    for (let i = paths.length - 1; i > -1; i--) {
      const path = paths[i];
      if (isAbsolutePath) {
        break;
      }
      if (!path) {
        continue
      }
      resolvePath = path + '/' + resolvePath;
      isAbsolutePath = path.charCodeAt(0) === 47;
    }
    if (/^\/+$/.test(resolvePath)) {
      resolvePath = resolvePath.replace(/(\/+)/, '/')
    } else {
      resolvePath = resolvePath.replace(/(?!^)\w+\/+\.{2}\//g, '')
        .replace(/(?!^)\.\//g, '')
        .replace(/\/+$/, '')
    }
    return resolvePath;
  }

}


export const Path = new BfsPath()
