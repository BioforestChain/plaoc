import { BfcsNavigator } from "./BfcsNavigator";

const builder = () => {
  const currentInfo = JSON.parse(navigator_ffi.init());
  return new BfcsNavigator(currentInfo.info, currentInfo.parent, navigator_ffi);
};
export default builder;
