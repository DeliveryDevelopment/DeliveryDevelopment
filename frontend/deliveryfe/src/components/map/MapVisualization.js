import React, { useEffect, useMemo, useState } from "react";
import { Card, Empty, Spin, Typography, Image } from "antd";

const { Text } = Typography;

// Example mock order data for MapVisualization component.
// This simulates a delivery route from USC Village to LAX Airport.

export const mockOrder = {
  startAddress: "3201 S Hoover St, Los Angeles, CA 90007",
  endAddress: "1 World Wy, Los Angeles, CA 90045",

  // The courier starts at USC Village at this time
  startTime: new Date(Date.now() - 3 * 60 * 1000).toISOString(),

  // Estimated arrival at LAX
  endTime: new Date(Date.now() + 12 * 60 * 1000).toISOString(),

  // Type of courier: "drone" or "robot"
  courierType: "drone",
};

const GOOGLE_MAPS_API_KEY = "AIzaSyB8pH2s9VWDlVIDCYR_0jkWIoeE5fCTmDE";

const geocodeAddress = async (address) => {
  if (!GOOGLE_MAPS_API_KEY) {
    throw new Error("Missing Google Maps API key");
  }
  const url = `https://maps.googleapis.com/maps/api/geocode/json?address=${encodeURIComponent(
    address
  )}&key=${GOOGLE_MAPS_API_KEY}`;

  const resp = await fetch(url);
  const data = await resp.json();

  if (data.status !== "OK" || !data.results?.length) {
    throw new Error(`Geocoding failed for address: ${address}`);
  }

  const { lat, lng } = data.results[0].geometry.location;
  return { lat, lng };
};

const MapVisualization = ({ order }) => {
  //mock:

  const [coords, setCoords] = useState({
    start: null,
    end: null,
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [now, setNow] = useState(Date.now());

  const { startAddress, endAddress, startTime, endTime, courierType } =
    order || {};

  // set current time, get est location
  useEffect(() => {
    const timer = setInterval(() => {
      setNow(Date.now());
    }, 18000000); // every 5 seconds to refresh

    return () => clearInterval(timer);
  }, []);

  // when address changing, call geocodeAddress func
  useEffect(() => {
    if (!startAddress || !endAddress) return;

    let cancelled = false;
    const fetchCoords = async () => {
      try {
        setLoading(true);
        setError("");
        const [start, end] = await Promise.all([
          geocodeAddress(startAddress),
          geocodeAddress(endAddress),
        ]);

        if (!cancelled) {
          setCoords({ start, end });
        }
      } catch (e) {
        console.error(e);
        if (!cancelled) {
          setError(e.message || "Failed to load map data");
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    };

    fetchCoords();

    return () => {
      cancelled = true;
    };
  }, [startAddress, endAddress]);

  // according to now / startTime / endTime, estimate location and progress.
  const { progress, currentPoint } = useMemo(() => {
    if (!coords.start || !coords.end || !startTime || !endTime) {
      return { progress: 0, currentPoint: null };
    }

    const startMs = new Date(startTime).getTime();
    const endMs = new Date(endTime).getTime();
    //console.log(startMs, new Date(startTime), startTime, " ||||  ", endMs);
    const nowMs = now;

    if (Number.isNaN(startMs) || Number.isNaN(endMs)) {
      return { progress: 0, currentPoint: null };
    }

    if (endMs <= startMs) {
      // invalid endpoint, return the end point
      return { progress: 1, currentPoint: coords.end };
    }

    let p = (nowMs - startMs) / (endMs - startMs);
    console.log(startMs, nowMs, endMs);
    if (p < 0) p = 0;
    if (p > 1) p = 1;

    const lat = coords.start.lat + (coords.end.lat - coords.start.lat) * p;
    const lng = coords.start.lng + (coords.end.lng - coords.start.lng) * p;

    return {
      progress: p,
      currentPoint: { lat, lng },
    };
  }, [coords.start, coords.end, startTime, endTime, now]);

  // generate URL of Google Static Map ，set the three points.
  const staticMapUrl = useMemo(() => {
    if (!coords.start || !coords.end || !currentPoint || !GOOGLE_MAPS_API_KEY) {
      return "";
    }

    const { start, end } = coords;

    // according to the path
    const pathColor = courierType === "drone" ? "0x0000FFAA" : "0x00AA00AA";

    // markers: start point (S)，current point (C)，end point (E)
    const params = [
      "size=400x200",
      "scale=2",
      "maptype=roadmap",
      `markers=color:green|label:S|${start.lat},${start.lng}`,
      `markers=color:blue|label:C|${currentPoint.lat},${currentPoint.lng}`,
      `markers=color:red|label:E|${end.lat},${end.lng}`,
      `path=color:${pathColor}|weight:4|${start.lat},${start.lng}|${currentPoint.lat},${currentPoint.lng}|${end.lat},${end.lng}`,
      `key=${GOOGLE_MAPS_API_KEY}`,
    ];

    return `https://maps.googleapis.com/maps/api/staticmap?${params.join("&")}`;
  }, [coords.start, coords.end, currentPoint, courierType]);

  return (
    <Card
      size="small"
      style={{ marginTop: 16 }}
      title="Route tracking"
      extra={
        courierType ? (
          <Text type="secondary">
            RouteType: {courierType === "drone" ? "Drone" : "Robot"}
          </Text>
        ) : null
      }
    >
      {!startAddress || !endAddress ? (
        <Empty description="lack the start or end point" />
      ) : error ? (
        <Empty description={error} />
      ) : loading || !coords.start || !coords.end || !currentPoint ? (
        <div style={{ textAlign: "center", padding: 24 }}>
          <Spin />
          <div style={{ marginTop: 8 }}>
            <Text type="secondary">Loading…</Text>
          </div>
        </div>
      ) : !staticMapUrl ? (
        <Empty description="the map is not available, check the Google API Key" />
      ) : (
        <>
          <div style={{ marginBottom: 8 }}>
            <Text>
              Start Site {startAddress}
              <br />
              End Site {endAddress}
              <br />
              Progress: {(progress * 100).toFixed(1)}%
            </Text>
          </div>
          <Image
            src={staticMapUrl} // this is the original map image
            alt="Route map"
            style={{
              width: "100%",
              height: 200, // Thumbnail height
              objectFit: "cover", // Crop to fit thumbnail area
              borderRadius: 4,
            }}
            preview={{
              src: staticMapUrl, // Use full image for preview (no distortion)
              mask: <span>Click to preview</span>,
              // Or mask: false for cleaner look
            }}
          />
        </>
      )}
    </Card>
  );
};

MapVisualization.defaultProps = {
  order: null,
};

export default MapVisualization;
