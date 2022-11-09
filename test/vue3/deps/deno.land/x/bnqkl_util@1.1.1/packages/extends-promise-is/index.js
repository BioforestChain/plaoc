/**
 * @param value
 * @returns
 * @inline
 */
export const isPromiseLike = (value) => {
    return (value instanceof Object &&
        typeof value.then === "function");
};
/**
 * @param value
 * @returns
 * @inline
 */
export const isPromise = (value) => {
    return value instanceof Promise;
};
