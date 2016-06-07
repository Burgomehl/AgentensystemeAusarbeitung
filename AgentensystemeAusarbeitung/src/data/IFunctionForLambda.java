package data;

@FunctionalInterface
public interface IFunctionForLambda<T> {
	void function(Cell a, T t)throws Exception;
}
