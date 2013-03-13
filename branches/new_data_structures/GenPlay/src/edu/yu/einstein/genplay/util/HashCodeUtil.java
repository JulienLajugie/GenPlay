package edu.yu.einstein.genplay.util;

import java.lang.reflect.Array;


/**
 * This class is highly inspired on the class made by Joshua Bloch (Effective Java).
 * Comments and reformatting changes have been made.
 * 
 * 
 * Collected methods which allow easy implementation of <code>hashCode</code>.
 *
 * Example use case:
 * <pre>
 *  public int hashCode(){
 *    int result = HashCodeUtil.SEED;
 *    //collect the contributions of various fields
 *    result = HashCodeUtil.hash(result, fPrimitive);
 *    result = HashCodeUtil.hash(result, fObject);
 *    result = HashCodeUtil.hash(result, fArray);
 *    return result;
 *  }
 * </pre>
 */
public final class HashCodeUtil {

	private static final int fODD_PRIME_NUMBER = 37;


	/**
	 * An initial value for a <code>hashCode</code>, to which is added contributions
	 * from fields. Using a non-zero value decreases collisons of <code>hashCode</code>
	 * values.
	 */
	public static final int SEED = 23;


	/**
	 * Hashes a boolean.
	 * @param aSeed the seed
	 * @param aBoolean a boolean
	 * @return the hash of the boolean
	 */
	public static int hash( int aSeed, boolean aBoolean ) {
		return firstTerm( aSeed ) + ( aBoolean ? 1 : 0 );
	}


	/**
	 * Hashes a char.
	 * @param aSeed the seed
	 * @param aChar a char
	 * @return the hash of the char
	 */
	public static int hash( int aSeed, char aChar ) {
		return firstTerm( aSeed ) + aChar;
	}


	/**
	 * Hashes a int.
	 * byte and short are handled by this method, through implicit conversion.
	 * @param aSeed the seed
	 * @param aInt a int
	 * @return the hash of the int
	 */
	public static int hash( int aSeed , int aInt ) {
		return firstTerm( aSeed ) + aInt;
	}


	/**
	 * Hashes a long.
	 * @param aSeed the seed
	 * @param aLong a long
	 * @return the hash of the long
	 */
	public static int hash( int aSeed , long aLong ) {
		return firstTerm(aSeed)  + (int)( aLong ^ (aLong >>> 32) );
	}


	/**
	 * Hashes a float.
	 * @param aSeed the seed
	 * @param aFloat a float
	 * @return the hash of the float
	 */
	public static int hash( int aSeed , float aFloat ) {
		return hash( aSeed, Float.floatToIntBits(aFloat) );
	}


	/**
	 * Hashes a double.
	 * @param aSeed the seed
	 * @param aDouble a double
	 * @return the hash of the double
	 */
	public static int hash( int aSeed , double aDouble ) {
		return hash( aSeed, Double.doubleToLongBits(aDouble) );
	}


	/**
	 * Hashes an Object.
	 * 
	 * <code>aObject</code> is a possibly-null object field, and possibly an array.
	 *
	 * If <code>aObject</code> is an array, then each element may be a primitive
	 * or a possibly-null object.
	 * @param aSeed the seed
	 * @param aObject an object
	 * @return the hash of the object
	 */
	public static int hash( int aSeed , Object aObject ) {
		int result = aSeed;
		if ( aObject == null) {
			result = hash(result, 0);
		}
		else if ( ! isArray(aObject) ) {
			result = hash(result, aObject.hashCode());
		}
		else {
			int length = Array.getLength(aObject);
			for ( int idx = 0; idx < length; ++idx ) {
				Object item = Array.get(aObject, idx);
				//recursive call!
				result = hash(result, item);
			}
		}
		return result;
	}


	private static int firstTerm( int aSeed ){
		return fODD_PRIME_NUMBER * aSeed;
	}

	private static boolean isArray(Object aObject){
		return aObject.getClass().isArray();
	}
}