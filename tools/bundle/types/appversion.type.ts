import { Files } from "@bfsx/metadata";

export interface IAppversion {
  data: {
    version: string;
    icon: string;
    name: string;
    files: Files[];
    releaseNotes: string;
    releaseName: string;
    releaseDate: string;
  };
  errorCode: number;
  errorMsg: string;
}

// {
//   "data": {
//     "version": "",
//     "icon": "",
//     "name": "",
//     "files": [
//       {
//         "url": "",
//         "size": 0,
//         "sha512": ""
//       }
//     ],
//     "releaseNotes": "",
//     "releaseName": "",
//     "releaseDate": ""
//   },
//   "errorCode": 0,
//   "errorMsg": "success"
// }
