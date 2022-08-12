/**
 * 获取文件后缀名
 * @param fileName string
 * @returns
 */
export const getExtension = (fileName: string) => {
  // console.log(fileName, fileName.lastIndexOf("."));
  return fileName.substring(fileName.lastIndexOf(".") + 1);
};
