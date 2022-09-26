export interface IsOption {
  filter: IsFilter[], //声明筛选方式
  recursive: boolean // 是否要递归遍历目录，默认是 false
}

/** * 声明筛选方式*/
export interface IsFilter {
  type: EFilterType,
  name: string[]
}

export enum EFilterType {
  file = "file",
  directroy = "directroy"
}

