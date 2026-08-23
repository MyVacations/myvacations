import {setGlobalOptions} from "firebase-functions/v2";
import {onCall, HttpsError} from "firebase-functions/v2/https";
import {onSchedule} from "firebase-functions/v2/scheduler";
import {initializeApp} from "firebase-admin/app";
import {getFirestore} from "firebase-admin/firestore";
import {createHmac} from "crypto";

const GOOGLE_PLACES_MONTHLY_LIMIT = 4500;
const MAX_REQUESTS_PER_DAY = 20;
const MAX_REQUESTS_PER_MINUTE = 5;
const USER_USAGE_RETENTION_DAYS = 30;

initializeApp();

const db = getFirestore();

setGlobalOptions({
  region: "europe-southwest1",
  maxInstances: 5,
});

/**
 * Extension for Google Main Tag
 * @param {string} label Google Places category to search for.
 * @return {string} return for Google Places.
 */
function predictionToGoogleType(
  label: string,
): string {
  switch (label) {
  case "ATTRACTION":
    return "tourist_attraction";

  case "BAR":
    return "bar";

  case "CAFE":
    return "cafe";

  case "CINEMA":
    return "movie_theater";

  case "FAST_FOOD":
    return "fast_food_restaurant";

  case "FUEL":
    return "gas_station";

  case "GALLERY":
    return "art_gallery";

  case "GARDEN":
    return "garden";

  case "HOSPITAL":
    return "hospital";

  case "MALL":
    return "shopping_mall";

  case "MUSEUM":
    return "museum";

  case "PARK":
    return "park";

  case "PHARMACY":
    return "pharmacy";

  case "PUB":
    return "pub";

  case "RESTAURANT":
    return "restaurant";

  case "SUPERMARKET":
    return "supermarket";

  case "THEATRE":
    return "performing_arts_theater";

  case "VIEWPOINT":
    return "observation_deck";

  default:
    return "";
  }
}

/**
 * Extension for Google Subcategory Tag
 * @param {string} subcategory Google Places subcategory to search for.
 * @return {string} return for Google Places.
 */
function predictionToGoogleSubcategory(
  subcategory: string
): string {
  switch (subcategory) {
  case "BURGER":
    return "hamburger_restaurant";

  case "CHINESE":
    return "chinese_restaurant";

  case "INDIAN":
    return "indian_restaurant";

  case "ITALIAN":
    return "italian_restaurant";

  case "JAPANESE":
    return "japanese_restaurant";

  case "KOREAN":
    return "korean_restaurant";

  case "MEXICAN":
    return "mexican_restaurant";

  case "PIZZA":
    return "pizza_restaurant";

  case "SEAFOOD":
    return "seafood_restaurant";

  case "SPANISH":
    return "spanish_restaurant";

  case "STEAKHOUSE":
    return "steak_house";

  case "THAI":
    return "thai_restaurant";

  case "VEGAN":
    return "vegan_restaurant";

  case "VEGETARIAN":
    return "vegetarian_restaurant";

  case "GENERAL":
    return "restaurant";

  default:
    return "";
  }
}

/**
 * Extension for OSM Main Tag
 * @param {string} label OSM Places category to search for.
 * @return {string} return for OSM Places.
 */
function predictionToOSMType(
  label: string,
): string {
  switch (label) {
  case "ATTRACTION":
    return "[\"tourism\"=\"attraction\"]";

  case "BAR":
    return "[\"amenity\"=\"bar\"]";

  case "CAFE":
    return "[\"amenity\"=\"cafe\"]";

  case "CINEMA":
    return "[\"amenity\"=\"cinema\"]";

  case "FAST_FOOD":
    return "[\"amenity\"=\"fast_food\"]";

  case "FUEL":
    return "[\"amenity\"=\"fuel\"]";

  case "GALLERY":
    return "[\"tourism\"=\"gallery\"]";

  case "GARDEN":
    return "[\"leisure\"=\"garden\"]";

  case "HOSPITAL":
    return "[\"amenity\"=\"hospital\"]";

  case "MALL":
    return "[\"shop\"=\"mall\"]";

  case "MUSEUM":
    return "[\"tourism\"=\"museum\"]";

  case "PARK":
    return "[\"leisure\"=\"park\"]";

  case "PHARMACY":
    return "[\"amenity\"=\"pharmacy\"]";

  case "PUB":
    return "[\"amenity\"=\"pub\"]";

  case "RESTAURANT":
    return "[\"amenity\"=\"restaurant\"]";

  case "SUPERMARKET":
    return "[\"shop\"=\"supermarket\"]";

  case "THEATRE":
    return "[\"amenity\"=\"theatre\"]";

  case "VIEWPOINT":
    return "[\"tourism\"=\"viewpoint\"]";

  default:
    return "";
  }
}
/**
 * Extension for OSM Subcategory Tag
 * @param {string} subcategory OSM Places subcategory to search for.
 * @return {string} return for Google Places.
 */
function predictionToOSMSubcategory(
  subcategory: string
): string {
  switch (subcategory) {
  case "BURGER":
    return "[\"amenity\"=\"restaurant\"][\"cuisine\"=\"burger\"]";

  case "CHINESE":
    return "[\"amenity\"=\"restaurant\"][\"cuisine\"=\"chinese\"]";

  case "INDIAN":
    return "[\"amenity\"=\"restaurant\"][\"cuisine\"=\"indian\"]";

  case "ITALIAN":
    return "[\"amenity\"=\"restaurant\"][\"cuisine\"=\"italian\"]";

  case "JAPANESE":
    return "[\"amenity\"=\"restaurant\"][\"cuisine\"=\"japanese\"]";

  case "KOREAN":
    return "[\"amenity\"=\"restaurant\"][\"cuisine\"=\"korean\"]";

  case "MEXICAN":
    return "[\"amenity\"=\"restaurant\"][\"cuisine\"=\"mexican\"]";

  case "PIZZA":
    return "[\"amenity\"=\"restaurant\"][\"cuisine\"=\"pizza\"]";

  case "SEAFOOD":
    return "[\"amenity\"=\"restaurant\"][\"cuisine\"=\"seafood\"]";

  case "SPANISH":
    return "[\"amenity\"=\"restaurant\"][\"cuisine\"=\"spanish\"]";

  case "STEAKHOUSE":
    return "[\"amenity\"=\"restaurant\"][\"cuisine\"=\"steak\"]";

  case "THAI":
    return "[\"amenity\"=\"restaurant\"][\"cuisine\"=\"thai\"]";

  case "VEGAN":
    return "[\"amenity\"=\"restaurant\"][\"diet:vegan\"=\"yes\"]";

  case "VEGETARIAN":
    return "[\"amenity\"=\"restaurant\"][\"diet:vegetarian\"=\"yes\"]";

  case "GENERAL":
    return "[\"amenity\"=\"restaurant\"]";

  default:
    return "";
  }
}

/**
 * Releases a reserved Google Places request.
 */
async function releaseGooglePlacesRequest(): Promise<void> {
  const globalRef = db
    .collection("usageGlobal")
    .doc("places");

  const currentMonth = new Date()
    .toISOString()
    .substring(0, 7);

  await db.runTransaction(async (transaction) => {
    const snapshot = await transaction.get(globalRef);

    if (!snapshot.exists) {
      return;
    }

    const data = snapshot.data();

    if (data?.month !== currentMonth) {
      return;
    }

    const currentRequests = data?.googleRequests ?? 0;

    if (currentRequests <= 0) {
      return;
    }

    transaction.update(globalRef, {
      googleRequests: currentRequests - 1,
    });
  });
}

/**
 * Reserves one Google Places request for the current month.
 */
async function reserveGooglePlacesRequest(): Promise<boolean> {
  const globalRef = db
    .collection("usageGlobal")
    .doc("places");

  const currentMonth = new Date()
    .toISOString()
    .substring(0, 7);

  return await db.runTransaction(async (transaction) => {
    const snapshot = await transaction.get(globalRef);

    if (!snapshot.exists) {
      transaction.set(globalRef, {
        month: currentMonth,
        googleRequests: 1,
      });

      return true;
    }

    const data = snapshot.data();

    if (data?.month !== currentMonth) {
      transaction.set(globalRef, {
        month: currentMonth,
        googleRequests: 1,
      });

      return true;
    }

    const currentRequests = data?.googleRequests ?? 0;

    if (currentRequests >= GOOGLE_PLACES_MONTHLY_LIMIT) {
      return false;
    }

    transaction.update(globalRef, {
      googleRequests: currentRequests + 1,
    });

    return true;
  });
}

/**
 * Searches nearby places using Google Places API (New).
 *
 * @param {number} latitude Latitude of the search center.
 * @param {number} longitude Longitude of the search center.
 * @param {string} label Google Places category to search for.
 * @param {string} subcategory Google Places subcategory to search for.
 */
async function searchNearbyPlaces(
  latitude: number,
  longitude: number,
  label: string,
  subcategory: string
) {
  const apiKey = process.env.GOOGLE_PLACES_API_KEY;

  if (!apiKey) {
    throw new HttpsError(
      "internal",
      "Google Places API key is not configured"
    );
  }

  const typeCategoryMapped = predictionToGoogleType(label);
  const typeSubcategoryMapped =
    label === "RESTAURANT" ?
      predictionToGoogleSubcategory(subcategory) :
      "";

  if (label === "NONE") {
    throw new HttpsError(
      "failed-precondition",
      "The requested query is not accessible"
    );
  }

  const typeToQuery =
    label === "RESTAURANT" && typeSubcategoryMapped !== null ?
      typeSubcategoryMapped :
      typeCategoryMapped;

  const response = await fetch(
    "https://places.googleapis.com/v1/places:searchNearby",
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-Goog-Api-Key": apiKey,
        "X-Goog-FieldMask":
          "places.id,places.displayName,places.location," +
          "places.formattedAddress,places.primaryType",
      },
      body: JSON.stringify({
        includedTypes: [typeToQuery],
        maxResultCount: 10,
        rankPreference: "DISTANCE",
        locationRestriction: {
          circle: {
            center: {
              latitude,
              longitude,
            },
            radius: 1000,
          },
        },
      }),
    }
  );

  if (!response.ok) {
    const errorBody = await response.text();

    console.error("Google Places error", {
      status: response.status,
      body: errorBody,
    });

    throw new HttpsError(
      "internal",
      "Google Places request failed"
    );
  }

  return await response.json();
}

/**
 * Searches nearby places using OpenStreetMap Overpass API.
 *
 * @param {number} latitude Latitude of the search center.
 * @param {number} longitude Longitude of the search center.
 * @param {string} label OpenStreetMap amenity category to search for.
 * @param {string} subcategory OpenStreetMap amenity subcategory to search for.
 */
async function searchNearbyOsmPlaces(
  latitude: number,
  longitude: number,
  label: string,
  subcategory: string
) {
  const typeCategoryMapped = predictionToOSMType(label);
  const typeSubcategoryMapped =
    label === "RESTAURANT" ?
      predictionToOSMSubcategory(subcategory) :
      "";

  if (label === "NONE") {
    throw new HttpsError(
      "failed-precondition",
      "The requested query is not accessible"
    );
  }

  const typeToQuery =
  label === "RESTAURANT" && typeSubcategoryMapped !== null ?
    typeSubcategoryMapped :
    typeCategoryMapped;

  const query = `
    [out:json];
    node${typeToQuery}(around:5000,${latitude},${longitude});
    out center;
    `;
  const response = await fetch(
    "https://overpass-api.de/api/interpreter",
    {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
        "User-Agent": "MyVacations/1.0",
      },
      body: "data=" + encodeURIComponent(query),
    }
  );

  const responseBody = await response.text();

  if (!response.ok) {
    throw new HttpsError(
      "internal",
      `OpenStreetMap request failed: ${response.status}`
    );
  }

  return JSON.parse(responseBody);
}

export const testBackend = onCall(
  {
    enforceAppCheck: true,
    secrets: [
      "GOOGLE_PLACES_API_KEY",
      "IP_HASH_SECRET",
    ],
  },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError(
        "unauthenticated",
        "Authentication required"
      );
    }

    const latitude = request.data?.latitude;
    const longitude = request.data?.longitude;
    const label = request.data?.label;
    const subcategory = request.data?.subcategory;

    if (
      typeof latitude !== "number" ||
      typeof longitude !== "number" ||
      typeof label !== "string" ||
      typeof subcategory !== "string"
    ) {
      throw new HttpsError(
        "invalid-argument",
        "latitude, longitude and type are required"
      );
    }

    const uid = request.auth.uid;

    const ip = request.rawRequest.ip;

    if (!ip) {
      throw new HttpsError(
        "internal",
        "Unable to determine client IP"
      );
    }

    const ipHashSecret = process.env.IP_HASH_SECRET;

    if (!ipHashSecret) {
      throw new HttpsError(
        "internal",
        "IP hash secret is not configured"
      );
    }

    const ipHash = createHmac(
      "sha256",
      ipHashSecret
    )
      .update(ip)
      .digest("hex");


    const userRef = db
      .collection("usageUsers")
      .doc(uid);

    const ipRef = db
      .collection("usageIps")
      .doc(ipHash);

    const today = new Date()
      .toISOString()
      .substring(0, 10);

    const minuteBucket = Math.floor(Date.now() / 60000);

    const {
      dailyCount,
      minuteCount,
      googleEnabled: userGoogleEnabled,
    } = await db.runTransaction(
      async (transaction) => {
        const userSnapshot = await transaction.get(userRef);
        const ipSnapshot = await transaction.get(ipRef);

        const userData = userSnapshot.exists ?
          userSnapshot.data() :
          undefined;

        const ipData = ipSnapshot.exists ?
          ipSnapshot.data() :
          undefined;

        // -------------------------
        // LÍMITE DIARIO
        // -------------------------

        if (ipData) {
          const ipDailyCount =
            ipData.dailyDate === today ?
              (ipData.dailyCount ?? 0) :
              0;

          const ipMinuteCount =
            ipData.minuteBucket === minuteBucket ?
              (ipData.minuteCount ?? 0) :
              0;

          if (ipDailyCount >= MAX_REQUESTS_PER_DAY) {
            return {
              dailyCount: ipDailyCount,
              minuteCount: ipData?.minuteCount ?? 0,
              googleEnabled: false,
            };
          }

          if (ipMinuteCount >= MAX_REQUESTS_PER_MINUTE) {
            return {
              dailyCount: ipDailyCount,
              minuteCount: ipMinuteCount,
              googleEnabled: false,
            };
          }
        }

        let newDailyCount: number;

        if (userData?.dailyDate === today) {
          const currentDailyCount = userData?.dailyCount ?? 0;

          if (currentDailyCount >= MAX_REQUESTS_PER_DAY) {
            return {
              dailyCount: currentDailyCount,
              minuteCount: userData?.minuteCount ?? 0,
              googleEnabled: false,
            };
          }

          newDailyCount = currentDailyCount + 1;
        } else {
          // Nuevo día
          newDailyCount = 1;
        }

        // -------------------------
        // LÍMITE POR MINUTO
        // -------------------------

        let newMinuteCount: number;

        if (userData?.minuteBucket === minuteBucket) {
          const currentMinuteCount = userData?.minuteCount ?? 0;

          if (currentMinuteCount >= MAX_REQUESTS_PER_MINUTE) {
            return {
              dailyCount: newDailyCount,
              minuteCount: currentMinuteCount,
              googleEnabled: false,
            };
          }

          newMinuteCount = currentMinuteCount + 1;
        } else {
          // Nuevo minuto
          newMinuteCount = 1;
        }

        // -------------------------
        // ACTUALIZAR FIRESTORE
        // -------------------------

        transaction.set(
          userRef,
          {
            dailyCount: newDailyCount,
            dailyDate: today,
            minuteCount: newMinuteCount,
            minuteBucket,
          },
          {merge: true}
        );
        if (!ipSnapshot.exists) {
          transaction.set(ipRef, {
            dailyCount: 1,
            dailyDate: today,
            minuteCount: 1,
            minuteBucket,
          });
        } else {
          const currentIpDailyCount =
              ipData?.dailyDate === today ?
                (ipData?.dailyCount ?? 0) :
                0;

          const newIpDailyCount = currentIpDailyCount + 1;

          const currentIpMinuteCount =
              ipData?.minuteBucket === minuteBucket ?
                (ipData?.minuteCount ?? 0) :
                0;

          const newIpMinuteCount = currentIpMinuteCount + 1;

          transaction.update(ipRef, {
            dailyCount: newIpDailyCount,
            dailyDate: today,
            minuteCount: newIpMinuteCount,
            minuteBucket,
          });
        }

        return {
          dailyCount: newDailyCount,
          minuteCount: newMinuteCount,
          googleEnabled: true,
        };
      }
    );

    // -------------------------
    // SELECCIÓN DEL PROVEEDOR
    // -------------------------

    let googleEnabled = false;

    if (userGoogleEnabled) {
      googleEnabled = await reserveGooglePlacesRequest();
    }

    let places = null;

    if (googleEnabled) {
      try {
        places = await searchNearbyPlaces(
          latitude,
          longitude,
          label,
          subcategory
        );
      } catch (error) {
        await releaseGooglePlacesRequest();
        throw error;
      }
    } else {
      places = await searchNearbyOsmPlaces(
        latitude,
        longitude,
        label,
        subcategory
      );
    }

    return {
      success: true,
      uid,
      appCheck: request.app !== undefined,
      dailyCount,
      minuteCount,
      googleEnabled,
      provider: googleEnabled ? "google" : "osm",
      places,
    };
  }
);

export const cleanupInactiveUsersV2 = onSchedule(
  {
    schedule: "0 3 * * *",
    timeZone: "Europe/Madrid",
    region: "europe-west1",
  },
  async () => {
    const cutoffDate = new Date();

    cutoffDate.setDate(
      cutoffDate.getDate() - USER_USAGE_RETENTION_DAYS
    );

    const cutoffDateString = cutoffDate
      .toISOString()
      .substring(0, 10);

    const snapshot = await db
      .collection("usageUsers")
      .where("dailyDate", "<", cutoffDateString)
      .get();

    if (snapshot.empty) {
      console.log("No inactive users to delete");
      return;
    }

    const batch = db.batch();

    snapshot.docs.forEach((doc) => {
      batch.delete(doc.ref);
    });

    await batch.commit();

    console.log(
      `Deleted ${snapshot.size} inactive users`
    );
  }
);
