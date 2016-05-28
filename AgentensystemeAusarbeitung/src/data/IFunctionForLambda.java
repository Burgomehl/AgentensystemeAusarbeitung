package data;

@FunctionalInterface
public interface IFunctionForLambda<T> {
	void function(Field a, T t)throws Exception;
}
